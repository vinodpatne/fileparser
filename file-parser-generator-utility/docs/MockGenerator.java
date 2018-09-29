package com.tivyso.testing.mock.generator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpressionImpl;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.tivyso.testing.mock.generator.MockMethodInfo.MethodMockType;
import com.tivyso.testing.mock.generator.componentfilter.ComponentFilterBridge;
import com.tivyso.testing.mock.generator.componentfilter.IComponentFilter;

/**
 * <b>example</b><br/>
 * <code>
 * 	MockGenerator mockGenerator = new MockGenerator();
 * 	mockGenerator.setBasePackage("java.lang");
 * 	mockGenerator.setOutputDir("./classes");
 * 	mockGenerator.loadComponents();
 * 	mockGenerator.generate();
 * 	mockGenerator.generateMockedModelFactory();
 * </code>
 * 
 * 
 */
public class MockGenerator {

	protected static final Logger LOG = Logger.getLogger(MockGenerator.class.getSimpleName());
	protected static final IMockedNameCreator DEFAULT_MOCKED_NAME_CREATOR = new IMockedNameCreator() {
		public String createName(String componentClassName) {
			if (componentClassName.startsWith("I")) {
				return new StringBuilder("T").append(componentClassName.substring(1)).toString();
			}
			return new StringBuilder("T").append(componentClassName).toString();
		}
	};
	protected static final IMockedPackageNameCreator DEFAULT_MOCKED_PACKAGE_NAME_CREATOR = new IMockedPackageNameCreator() {
		public String createPackageName(String componentPackageName) {
			return componentPackageName;
		}
	};

	/**
	 * path to ouput directory, where to generate source code
	 */
	protected String outputDir = DEFAULT_OUTPUT_DIR;

	/**
	 * base package to search components
	 */
	protected String basePackage = DEFAULT_BASE_PACKAGE;

	/**
	 * metadata of found components
	 */
	protected Set<BeanDefinition> components;

	protected List<MockClassInfo> mockClasses;

	/**
	 * filters to be used when searching for components. Include
	 */
	protected List<IComponentFilter> componentIncludeFilters;
	
	/**
	 * filters to be used when searching for components. Exclude
	 */
	protected List<IComponentFilter> componentExcludeFilters;

	/**
	 * to create class name of generated mock class from component`s class name
	 */
	protected IMockedNameCreator mockedNameCreator = DEFAULT_MOCKED_NAME_CREATOR;

	/**
	 * to create package name of generated mock class from component`s package name
	 */
	protected IMockedPackageNameCreator mockedPackageNameCreator = DEFAULT_MOCKED_PACKAGE_NAME_CREATOR;

	private static final String MOCKED_MODEL_FACTORY = "MockedModelFactory";
	private static final String DEFAULT_OUTPUT_DIR = "./generated-mocked-model";
	private static final String DEFAULT_BASE_PACKAGE = "com.model";

	private static final Set<Class<?>> COLLECTION_TYPES = new HashSet<Class<?>>() {
		private static final long serialVersionUID = 1888553512807755802L;
		{
			add(java.util.List.class);
			add(java.util.Set.class);
			add(java.util.Collection.class);
		}
	};
	private static final String FACTORY_METHOD_NAME = "create";
	private Map<Class<?>, GenericClass> genericClassRegister = new HashMap<Class<?>, GenericClass>();
	private Set<String> uniqueMethodsChecker;

	/**
	 * base package to search components
	 */
	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	/**
	 * path to ouput directory, where to generate source code
	 */
	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	/**
	 * to create class name of generated mock class from component`s class name
	 */

	public void setMockedNameCreator(IMockedNameCreator mockedNameCreator) {
		this.mockedNameCreator = mockedNameCreator;
	}

	/**
	 * to create package name of generated mock class from component`s package name
	 */
	public void setMockedPackageNameCreator(IMockedPackageNameCreator mockedPackageNameCreator) {
		this.mockedPackageNameCreator = mockedPackageNameCreator;
	}

	/**
	 * write log on DEBUG level if is debug enabled
	 */
	protected static void logDebug(Object o) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(o);
		}
	}

	private GenericClass getGenericClass(Class<?> clazz) {
		if (this.genericClassRegister.containsKey(clazz)) {
			return this.genericClassRegister.get(clazz);
		}
		GenericClass gc = new GenericClass(clazz);
		this.genericClassRegister.put(clazz, gc);
		return gc;
	}

	/**
	 * adds filter to be used when searching for components. INCLUDE
	 * 
	 * @returns (this) - to be fluent
	 */
	public MockGenerator addComponentIncludeFilter(IComponentFilter includeFilter) {
		if (this.componentIncludeFilters == null) {
			this.componentIncludeFilters = new ArrayList<IComponentFilter>();
		}
		this.componentIncludeFilters.add(includeFilter);
		return this;
	}
	
	/**
	 * adds filter to be used when searching for components. EXCLUDE
	 * 
	 * @returns (this) - to be fluent
	 */
	public MockGenerator addComponentExcludeFilter(IComponentFilter includeFilter) {
		if (this.componentExcludeFilters == null) {
			this.componentExcludeFilters = new ArrayList<IComponentFilter>();
		}
		this.componentExcludeFilters.add(includeFilter);
		return this;
	}

	/**
	 * loades components to generate mocks. this method must be called before method
	 * <b>generate()</b> is called
	 */
	public void loadComponents() {
		ClassPathScanningCandidateComponentProvider provider = new ComponentProvider(false);
		if (this.componentExcludeFilters != null) {
			for (IComponentFilter componentFilter : this.componentExcludeFilters) {
				provider.addExcludeFilter(new ComponentFilterBridge(componentFilter));
			}
		}
		if (MockGenerator.this.componentIncludeFilters == null) {
			provider.addIncludeFilter(new IncludeAllTypeFilter());
		} else {
			for (IComponentFilter componentFilter : this.componentIncludeFilters) {
				provider.addIncludeFilter(new ComponentFilterBridge(componentFilter));
			}
		}

		logDebug("searching for components. base package: " + this.basePackage);
		this.components = provider.findCandidateComponents(this.basePackage);
		logDebug("number of components found: " + this.components.size());
		loadMockClasses();
	}

	protected void loadMockClasses() {
		this.mockClasses = new ArrayList<MockClassInfo>();
		for (BeanDefinition beanDefinition : this.components) {
			MockClassInfo mockClassInfo = createMockClassInfoFromComponent(beanDefinition);
			if (mockClassInfo != null) {
				this.mockClasses.add(mockClassInfo);
			}
		}
	}

	/**
	 * creates mock class Info from components`s definition
	 */
	protected MockClassInfo createMockClassInfoFromComponent(BeanDefinition beanDefinition) {
		try {
			MockClassInfo mockClassInfo = new MockClassInfo();
			String beanClassName = beanDefinition.getBeanClassName();
			Class<?> componentClass = Class.forName(beanClassName);
			mockClassInfo.setComponentClass(componentClass);
			String mockedName = (this.mockedNameCreator == null ? componentClass.getSimpleName()
					: this.mockedNameCreator.createName(componentClass.getSimpleName()));
			mockClassInfo.setName(mockedName);
			String mockPackageName = (this.mockedPackageNameCreator == null ? componentClass.getPackage().getName()
					: this.mockedPackageNameCreator.createPackageName(componentClass.getPackage().getName()));
			mockClassInfo.setPackageName(mockPackageName);
			mockClassInfo.setMethods(new ArrayList<MockMethodInfo>());
			List<Method> methods = getComponentMethods(componentClass);
			if (methods != null) {
				for (Method method : methods) {
					MockMethodInfo mockMethodInfo = new MockMethodInfo();
					mockMethodInfo.setMethod(method);
					mockMethodInfo.setGetter(method.getName().startsWith("get"));
					mockMethodInfo.setMethodMockType(getMethodMockTypeForMethod(method));
					mockClassInfo.getMethods().add(mockMethodInfo);
				}
			}
			return mockClassInfo;
		} catch (Exception ex) {
			LOG.error("error creating MockClassInfo", ex);
			return null;
		}
	}

	protected static MethodMockType getMethodMockTypeForMethod(Method method) {
		MethodMockType methodMockType = null;
		if (method.getParameterTypes().length == 0 && !method.getReturnType().equals(Void.TYPE)) {
			if (COLLECTION_TYPES.contains(method.getReturnType())) {
				if (java.util.List.class.equals(method.getReturnType())) {
					methodMockType = MethodMockType.COLLECTION_LIST;
				} else if (java.util.Set.class.equals(method.getReturnType())) {
					methodMockType = MethodMockType.COLLECTION_SET;
				} else if (java.util.Collection.class.equals(method.getReturnType())) {
					methodMockType = MethodMockType.COLLECTION_COLLECTION;
				} else {
					methodMockType = MethodMockType.UNSUPPORTED;
				}
			} else {
				methodMockType = MethodMockType.SCALAR;
			}
		} else {
			methodMockType = MethodMockType.UNSUPPORTED;
		}
		return methodMockType;
	}

	private static void createDirectory(String outputDir) throws IllegalAccessException {
		String normalized = FilenameUtils.separatorsToSystem(outputDir);
		File dir = new File(normalized);
		if (!dir.exists() && !dir.mkdirs()) {
			throw new IllegalAccessException("could nod create directory: " + normalized);
		}
	}

	/**
	 * call method <b>loadComponents</b> befor calling this method generate
	 * 
	 * @throws Exception
	 */
	public void generate() throws IllegalAccessException {
		if (this.mockClasses == null) {
			LOG.warn("collections of components to be generated is null. call the loadComponents() method first.");
		}
		if (this.mockClasses != null) {
			createDirectory(outputDir);
			for (MockClassInfo mockClassInfo : this.mockClasses) {
				generateClassFromMockClassInfo(mockClassInfo);
			}
		}
	}

	/**
	 * generates mocked model factory class
	 */
	public void generateMockedModelFactory() {
		Exception ex = null;
		try {
			JCodeModel factoryCodeModel = new JCodeModel();
			String factoryPackageName = mockedPackageNameCreator.createPackageName(this.basePackage);
			JPackage factoryPackage = factoryCodeModel._package(factoryPackageName);

			createDirectory(outputDir);
			JDefinedClass factoryDefinedClass = factoryPackage._class(MOCKED_MODEL_FACTORY);

			for (MockClassInfo mockClassInfo : this.mockClasses) {
				JCodeModel jCodeModel = new JCodeModel();
				String mockedClassPackage = mockedPackageNameCreator.createPackageName(mockClassInfo.getComponentClass().getPackage().getName());
				JPackage jp = jCodeModel._package(mockedClassPackage);
				JDefinedClass jc = jp._class(mockClassInfo.getName());

				final String mockedName = mockClassInfo.getName();
				String factoryMethodName = new StringBuilder(mockedName.substring(0, 1).toUpperCase()).append(
						mockedName.substring(1)).toString();
				JMethod jMethod = factoryDefinedClass.method(JMod.PUBLIC, jc, factoryMethodName);
				jMethod.body()._return(
						new JExpressionSimple(new StringBuilder(mockedClassPackage).append(".").append(mockedName).append(".").append(FACTORY_METHOD_NAME)
								.append("()").toString()));
			}

			factoryCodeModel.build(new File(outputDir));
		} catch (IllegalAccessException e) {
			ex = e;
		} catch (IOException e) {
			ex = e;
		} catch (JClassAlreadyExistsException e) {
			ex = e;
		} finally {
			if (ex != null) {
				LOG.error("error generating Mocked Model Factory", ex);
			}
		}
	}

	/**
	 * generates class from mocked class info
	 */
	private void generateClassFromMockClassInfo(final MockClassInfo mockClassInfo) {
		Exception ex = null;
		try {
			JCodeModel jCodeModel = new JCodeModel();
			JPackage jp = jCodeModel._package(mockClassInfo.getPackageName());
			JMethod jMethod;
			JDefinedClass jc = jp._class(mockClassInfo.getName());

			// implemented interface
			jc._implements(mockClassInfo.getComponentClass());
			jCodeModel.ref(mockClassInfo.getComponentClass());

			// constructor
			JMethod ctor = jc.constructor(JMod.PROTECTED);
			ctor.body().directStatement("// nothing to do");

			// factory method
			jMethod = jc.method(JMod.PUBLIC | JMod.STATIC, jc, FACTORY_METHOD_NAME);
			jMethod.body()._return(
					new JExpressionSimple(new StringBuilder("new ").append(mockClassInfo.getName()).append("()")
							.toString()));

			this.uniqueMethodsChecker = new HashSet<String>();
			// methods
			for (MockMethodInfo mockMethodInfo : mockClassInfo.getMethods()) {
				if (mockMethodInfo != null) {
					JFieldVar jFieldVar = null;
					JFieldVar jFieldVarConfigured = null;
					if (mockMethodInfo.getMethodMockType() != null
							&& !MethodMockType.UNSUPPORTED.equals(mockMethodInfo.getMethodMockType())) {

						// fields
						Class<?> returnType = resolveReturnTypeClass(mockClassInfo.getComponentClass(), mockMethodInfo.getMethod());
						String fieldName = createFieldName(mockMethodInfo, returnType);
						String fieldConfiguredName = createFieldConfiguredName(mockMethodInfo, returnType);
						jFieldVar = jc.field(JMod.PRIVATE, returnType, fieldName);
						jFieldVarConfigured = jc.field(JMod.PRIVATE, Boolean.TYPE, fieldConfiguredName,
								new JExpressionSimple("false"));

						// method . supported . configuration method
						generateConfigMethod(jc, mockMethodInfo, jFieldVar, jFieldVarConfigured, returnType);
					}
					GenerateMethodParams params = new GenerateMethodParams();
					params.setjCodeModel(jCodeModel);
					params.setComponentClass(mockClassInfo.getComponentClass());
					params.setJc(jc);
					params.setMockMethodInfo(mockMethodInfo);
					params.setjFieldVar(jFieldVar);
					params.setjFieldVarConfigured(jFieldVarConfigured);
					generateMethod(params);
				}
			}
			jCodeModel.build(new File(outputDir));
		} catch (RuntimeException e) {
			ex = e;
		} catch (IOException e) {
			ex = e;
		} catch (JClassAlreadyExistsException e) {
			ex = e;
		} finally {
			if (ex != null) {
				LOG.error("error generating Mocked Model Factory", ex);
			}
		}
	}

	/**
	 * create field name for field to hold value of method`s return value<br/>
	 * Object getValue() -> "fieldValue"
	 */
	protected static String createFieldName(MockMethodInfo mockMethodInfo, Class<?> fieldType) {
		Validate.notNull(mockMethodInfo);
		Validate.notNull(mockMethodInfo.getMethod());
		String methodName = mockMethodInfo.getMethod().getName();
		String typeName = fieldType.getSimpleName().replace("[]", "_");
		if (mockMethodInfo.isGetter()) {
			int getMethodStartNameIndex = 3;
			return new StringBuilder("field").append(typeName).append(methodName.substring(getMethodStartNameIndex))
					.toString();
		}
		return new StringBuilder("field").append(typeName).append(methodName.substring(0, 1).toUpperCase()).append(
				methodName.substring(1)).toString();
	}

	/**
	 * create name of field to hold boolean value if value is already configured <br/>
	 * Object getValue() -> "fieldValueConfigured"
	 */
	protected static String createFieldConfiguredName(MockMethodInfo mockMethodInfo, Class<?> fieldType) {
		Validate.notNull(mockMethodInfo);
		Validate.notNull(mockMethodInfo.getMethod());
		return new StringBuilder(createFieldName(mockMethodInfo, fieldType)).append("Configured").toString();
	}

	/**
	 * create configuration method name <br/>
	 * Object getValue() -> "withValue"
	 */
	protected static String createConfigMethodName(MockMethodInfo mockMethodInfo) {
		Validate.notNull(mockMethodInfo);
		Validate.notNull(mockMethodInfo.getMethod());
		String methodName = mockMethodInfo.getMethod().getName();
		if (mockMethodInfo.isGetter()) {
			int getMethodStartNameIndex = 3;
			return new StringBuilder("with").append(methodName.substring(getMethodStartNameIndex)).toString();
		}
		return new StringBuilder("with").append(methodName.substring(0, 1).toUpperCase()).append(
				methodName.substring(1)).toString();
	}

	/**
	 * example: <code>
	 * 	public TClass withName(Object nameParam){
	 *  	fieldName = nameParam;
	 *  	fieldNameConfigured = true;
	 *  	return this;
	 *  }
	 *  </code>
	 */
	protected static JMethod generateConfigMethod(JDefinedClass jc, MockMethodInfo mockMethodInfo, JFieldVar jFieldVar,
			JFieldVar jFieldVarConfigured, Class<?> parameterType) {
		String configMethodName = createConfigMethodName(mockMethodInfo);
		JMethod jMethod = jc.method(JMod.PUBLIC, jc, configMethodName);
		String fieldName = jFieldVar.name();
		final String paramName = new StringBuilder(fieldName).append("Param").toString();
		if (MethodMockType.SCALAR.equals(mockMethodInfo.getMethodMockType())) {
			jMethod.param(parameterType, paramName);
			jMethod.body().assign(jFieldVar, new JExpressionSimple(paramName));
			jMethod.body().assign(jFieldVarConfigured, new JExpressionSimple("true"));
			jMethod.body()._return(new JExpressionSimple("this"));
		} else {
			Class<?> genericType = Object.class;
			if (ParameterizedType.class.isInstance(mockMethodInfo.getMethod().getGenericReturnType())) {
				ParameterizedType parameterizedType = ParameterizedType.class.cast(mockMethodInfo.getMethod()
						.getGenericReturnType());
				Type[] generArguments = parameterizedType.getActualTypeArguments();
				if (generArguments.length == 1 && Class.class.isInstance(generArguments[0])) {
					genericType = Class.class.cast(generArguments[0]);
				}
			}

			jMethod.varParam(genericType, paramName);
			jMethod.body().directStatement(
					new StringBuilder("java.util.List returnList = new java.util.ArrayList(java.util.Arrays.asList(")
							.append(paramName).append("));").toString());

			String returnListCode = getReturnListCodeByMockMethodInfo(mockMethodInfo);
			jMethod.body().directStatement(new StringBuilder(fieldName).append(returnListCode).toString());
			jMethod.body().assign(jFieldVarConfigured, new JExpressionSimple("true"));
			jMethod.body()._return(new JExpressionSimple("this"));
		}
		return jMethod;
	}

	protected static String getReturnListCodeByMockMethodInfo(MockMethodInfo mockMethodInfo) {
		String returnListCode = null;
		if (MethodMockType.COLLECTION_LIST.equals(mockMethodInfo.getMethodMockType())
				|| MethodMockType.COLLECTION_COLLECTION.equals(mockMethodInfo.getMethodMockType())) {
			returnListCode = " = returnList;";
		} else if (MethodMockType.COLLECTION_SET.equals(mockMethodInfo.getMethodMockType())) {
			returnListCode = " = new java.util.HashSet(returnList);";
		}
		return returnListCode;
	}

	protected Class<?> resolveReturnTypeClass(Class<?> componentClass, Method method) {
		Class<?> returnTypeClass;
		boolean methodIsGeneric = TypeVariable.class.isInstance(method.getGenericReturnType());
		if (methodIsGeneric) {
			returnTypeClass = resolveGenericTypeClass(componentClass, method, method.getGenericReturnType());
		} else {
			returnTypeClass = method.getReturnType();
		}
		return returnTypeClass;
	}

	/**
	 * example: public Object getName(){ if (!fieldNameConfigured){ throw new
	 * UnsupportedOperationException("..."); } return fieldName; }
	 * 
	 */
	protected JMethod generateMethod(GenerateMethodParams params) {
		Class<?> componentClass = params.getComponentClass();
		MockMethodInfo mockMethodInfo = params.getMockMethodInfo();
		Method method = mockMethodInfo.getMethod();

		JMethod jMethod = null;
		Class<?> returnTypeClass = resolveReturnTypeClass(componentClass, method);

		if (mockMethodInfo.getMethodMockType() != null
				&& !MethodMockType.UNSUPPORTED.equals(mockMethodInfo.getMethodMockType())) {
			jMethod = generateSupportedMethod(params, returnTypeClass);
		} else { // unsupported method
			generateUnsupportedMethod(params, returnTypeClass);
		}
		return jMethod;
	}

	/**
	 * example: public Object getName(){ if (!fieldNameConfigured){ throw new
	 * UnsupportedOperationException("..."); } return fieldName; }
	 * 
	 */
	protected JMethod generateSupportedMethod(GenerateMethodParams params, Class<?> returnTypeClass) {
		JDefinedClass jc = params.getJc();
		MockMethodInfo mockMethodInfo = params.getMockMethodInfo();
		JFieldVar jFieldVar = params.getjFieldVar();
		JFieldVar jFieldVarConfigured = params.getjFieldVarConfigured();
		Method method = mockMethodInfo.getMethod();

		JMethod jMethod;
		jMethod = jc.method(JMod.PUBLIC, returnTypeClass, method.getName());
		jMethod.javadoc().add(">>> " + method.getDeclaringClass());
		if (method.getExceptionTypes() != null && method.getExceptionTypes().length > 0) {
			Class cex = method.getExceptionTypes()[0];
			jMethod._throws(cex);
		}
		jMethod.body()._if(new JExpressionSimple(new StringBuilder("!").append(jFieldVarConfigured.name()).toString()))
				._then()._throw(
						new JExpressionSimple(new StringBuilder().append(
								"new UnsupportedOperationException(\"No mocked behaviour was specified for ").append(
								jc.name()).append(".").append(method.getName()).append("\")").toString()));
		jMethod.body()._return(new JExpressionSimple(new StringBuilder("this.").append(jFieldVar.name()).toString()));
		return jMethod;
	}

	/**
	 * method body: UnsupportedOperationException("..."); }
	 */
	protected JMethod generateUnsupportedMethod(GenerateMethodParams params, Class<?> returnTypeClass) {
		MockMethodInfo mockMethodInfo = params.getMockMethodInfo();
		Method method = mockMethodInfo.getMethod();
		Class<?> componentClass = params.getComponentClass();
		JDefinedClass jc = params.getJc();

		JMethod jMethod = null;
		List<Class<?>> paramsClasses = new ArrayList<Class<?>>();
		Type[] generParams = method.getGenericParameterTypes();
		if (generParams != null) {
			for (int i = 0; i < generParams.length; i++) {
				Type generParamType = generParams[i];
				Class<?> paramTypeClass = Object.class;
				if (TypeVariable.class.isInstance(generParamType)) { // parameter is generic
					paramTypeClass = resolveGenericTypeClass(componentClass, method, generParamType);
				} else {
					if (ParameterizedType.class.isInstance(generParamType)) {
						ParameterizedType pt = ParameterizedType.class.cast(generParamType);
						Type rawType = pt.getRawType();
						if (Class.class.isInstance(rawType)) {
							paramTypeClass = Class.class.cast(rawType);
						}
					}
					if (Class.class.isInstance(generParamType)) {
						paramTypeClass = Class.class.cast(generParamType);
					}
				}

				paramsClasses.add(paramTypeClass);
			}
		}
		String methodUniqueKey = returnTypeClass.getName() + "_" + method.getName();
		StringBuilder sb = new StringBuilder(methodUniqueKey);
		for (Class<?> paramClass : paramsClasses) {
			sb.append("-").append(paramClass.getName());
		}
		methodUniqueKey = sb.toString();

		if (this.uniqueMethodsChecker != null && !this.uniqueMethodsChecker.contains(methodUniqueKey)) {
			jMethod = jc.method(JMod.PUBLIC, returnTypeClass, method.getName());
			jMethod.javadoc().add(">>> " + method.getDeclaringClass());
			jMethod.javadoc().add("Unsupported. No mocked behaviour was specified.");
			jMethod.javadoc().addThrows(UnsupportedOperationException.class);
			if (paramsClasses != null) {
				for (int i = 0; i < paramsClasses.size(); i++) {
					jMethod.param(paramsClasses.get(i), new StringBuilder("arg").append(i).toString());
				}
			}
			jMethod.body()._throw(
					new JExpressionSimple(new StringBuilder().append(
							"new UnsupportedOperationException(\"No mocked behaviour was specified for ").append(
							jc.name()).append(".").append(method.getName()).append("\")").toString()));
			this.uniqueMethodsChecker.add(methodUniqueKey);
		}
		return jMethod;
	}

	/**
	 * resolves method`s return or param type, if generic
	 */
	private Class<?> resolveGenericTypeClass(Class<?> componentClass, Method method, Type typeToBeResolved) {
		Class<?> returnTypeClass = null;
		if (method.getDeclaringClass().equals(componentClass)) {
			returnTypeClass = resolveGenericTypeOfComponent(method, typeToBeResolved);
		} else { // declaring class is not component class
			returnTypeClass = resolveGenericTypeOfComponentsSuperClass(componentClass, method, typeToBeResolved);
		}
		return returnTypeClass;
	}

	/**
	 * resolves method`s return or param type, if generic if declaring class is component class
	 */
	private Class<?> resolveGenericTypeOfComponent(Method method, Type typeToBeResolved) {
		Class<?> returnTypeClass = null;
		if (TypeVariable.class.isInstance(typeToBeResolved)) {
			TypeVariable<?> typeVariable = TypeVariable.class.cast(typeToBeResolved);
			returnTypeClass = resolveClassOfTypeVariable(method.getDeclaringClass().getTypeParameters(), typeVariable);
		}
		if (returnTypeClass == null) {
			returnTypeClass = Object.class;
		}
		return returnTypeClass;
	}

	/**
	 * resolves method`s return or param type, if generic if declaring class is not component class
	 */
	private Class<?> resolveGenericTypeOfComponentsSuperClass(Class<?> componentClass, Method method,
			Type typeToBeResolved) {
		Class<?> returnTypeClass = null;
		GenericClass gc = getGenericClass(componentClass);
		TypeVariable<?> typeVariable = TypeVariable.class.cast(typeToBeResolved);

		GenericClass.ResultInfo genericResult = gc.resolveType(method.getDeclaringClass(), typeVariable.getName());
		Type resolvedType = genericResult.getType();
		Class<?> resolvedClass = genericResult.getClazz();

		if (TypeVariable.class.isInstance(resolvedType)) {
			TypeVariable<?> ttypeVariable = TypeVariable.class.cast(resolvedType);
			returnTypeClass = resolveClassOfTypeVariable(resolvedClass.getTypeParameters(), ttypeVariable);
		}
		if (Class.class.isInstance(resolvedType)) {
			returnTypeClass = Class.class.cast(resolvedType);
		}
		if (returnTypeClass == null) {
			returnTypeClass = Object.class;
		}
		return returnTypeClass;
	}

	private Class<?> resolveClassOfTypeVariable(TypeVariable<?>[] types, TypeVariable<?> typeVariable) {
		Class<?> returnTypeClass = null;
		for (Object type : types) {
			TypeVariable<?> tv = TypeVariable.class.cast(type);
			if (tv.getName().equals(typeVariable.getName()) && (tv.getBounds().length > 0)) {
				Type parentType = tv.getBounds()[0];
				if (Class.class.isInstance(parentType)) {
					returnTypeClass = Class.class.cast(parentType);
				}
				if (ParameterizedType.class.isInstance(parentType)) {
					ParameterizedType pt = ParameterizedType.class.cast(parentType);
					Type rawType = pt.getRawType();
					if (Class.class.isInstance(rawType)) {
						returnTypeClass = Class.class.cast(rawType);
					}
				}
			}
		}
		return returnTypeClass;
	}

	/**
	 * get methods from component to be generated
	 */
	protected static List<Method> getComponentMethods(Class<?> clazz) {
		List<Method> methods = new ArrayList<Method>();
		Set<String> uniqueMethods = new HashSet<String>();
		for (Method m : clazz.getMethods()) {
			String methodKey = m.getName();
			StringBuilder sb = new StringBuilder(methodKey);
			for (Class<?> paramType : m.getParameterTypes()) {
				sb.append("-").append(paramType.getName());
			}
			methodKey = sb.toString();
			if (!uniqueMethods.contains(methodKey)) {
				uniqueMethods.add(methodKey);
				methods.add(m);
			}
		}
		return methods;
	}

	private static class JExpressionSimple extends JExpressionImpl {
		private String code;

		public JExpressionSimple(String code) {
			super();
			this.code = code;
		}

		public void generate(JFormatter jFormatter) {
			jFormatter.p(code);
		}
	}

	public static class ComponentProvider extends ClassPathScanningCandidateComponentProvider {
		public ComponentProvider(boolean useDefaultFilters) {
			super(useDefaultFilters);
		}

		@Override
		protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
			boolean isInterface = beanDefinition.getMetadata().isInterface()
					&& beanDefinition.getMetadata().isIndependent();
			return isInterface;
		}
	}

	public static class IncludeAllTypeFilter implements TypeFilter {
		public boolean match(MetadataReader arg0, MetadataReaderFactory arg1) throws IOException {
			return true;
		}
	}

}
