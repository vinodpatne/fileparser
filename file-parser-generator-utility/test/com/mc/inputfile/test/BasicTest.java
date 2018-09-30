/*
 * Copyright 2004 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mc.inputfile.test;

import java.util.Date;

import com.ancientprogramming.fixedformat4j.format.FixedFormatManager;
import com.ancientprogramming.fixedformat4j.format.impl.FixedFormatManagerImpl;
import com.mc.inputfile.amex.records.MyPerson;

/**
 * Basic usage testing.
 */
public class BasicTest {

    private static FixedFormatManager manager = new FixedFormatManagerImpl();

    public static void main(String[] args) {
        // String string = "string 001232008-05-29";
        // BasicRecord record = manager.load(BasicRecord.class, string);
        MyPerson record = new MyPerson();

        record.setHeader("head");
        record.setAge(10);
        record.setFirstname("Vinod");
        record.setLastname("Patne");
        record.setJoiningDate(new Date());

        System.out.println("record: " + record);

        System.out.println("Exported: [" + manager.export(record) + "]");

        String testData = manager.export(record);

        MyPerson myRecord1 = manager.load(MyPerson.class, testData);

        String testData1 = manager.export(myRecord1);

        System.out.println("testData.equals(testData1)=>" + testData.equals(testData1));

    }

    // public static void testExportRecordObject() {
    // String data = "some text 0012320080514CT001100000010350000002056-0012 01200000002056";
    // Calendar someDay = Calendar.getInstance();
    // someDay.set(2008, 4, 14, 0, 0, 0);
    // someDay.set(Calendar.MILLISECOND, 0);
    //
    // MyRecord myRecord = new MyRecord();
    // myRecord.setBooleanData(true);
    // myRecord.setCharData('C');
    // myRecord.setDateData(someDay.getTime());
    // myRecord.setDoubleData(10.35);
    // myRecord.setFloatData(20.56F);
    // myRecord.setLongData(11L);
    // myRecord.setIntegerData(123);
    // myRecord.setStringData("some text ");
    // myRecord.setBigDecimalData(new BigDecimal(-12.012));
    // myRecord.setSimpleFloatData(20.56F);
    // System.out.println("test Exported: [" + manager.export(myRecord) + "]");
    //
    // String testData = "some text +012320080514C1XX11+0010;350000020;5600-0012;01200000002056";
    //
    // MyRecord myRecord1 = manager.load(MyRecord.class, testData);
    //
    // System.out.println("myRecord.equals(myRecord1)=" + myRecord.equals(myRecord1));
    //
    // // Assert.assertEquals("wrong record exported", data, manager.export(myRecord));
    //
    // MyRecord.MyStaticNestedClass myStaticNestedClass2 = new MyRecord.MyStaticNestedClass();
    // myStaticNestedClass2.setStringData("xyz");
    // String exportedString2 = manager.export(myStaticNestedClass2);
    // System.out.println("Exported: [" + exportedString2 + "]");
    // Assert.assertEquals("xyz ", exportedString2);
    //
    // }
    //
    // public static void testExportAnnotatedInnerClass() {
    // MyRecord myRecord = new MyRecord();
    // MyRecord.MyInnerClass myInnerClass = myRecord.new MyInnerClass();
    // myInnerClass.setStringData("xyz");
    // String exportedString = manager.export(myInnerClass);
    // Assert.assertEquals("xyz ", exportedString);
    //
    // NoDefaultConstructorClass noDefaultConstructorClass = new NoDefaultConstructorClass("foobar");
    // NoDefaultConstructorClass.MyInnerClass myInnerClass2 = noDefaultConstructorClass.new MyInnerClass();
    // myInnerClass2.setStringData("Vinod ");
    // exportedString = manager.export(myInnerClass2);
    // System.out.println("Exported: [" + exportedString + "]");
    // Assert.assertEquals("Vinod ", exportedString);
    // }
}
