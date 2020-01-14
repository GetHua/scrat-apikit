package org.forkjoin.scrat.apikit.example.form;

import org.forkjoin.TestForm;
import org.forkjoin.scrat.apikit.example.TestArrayForm;
import org.forkjoin.scrat.apikit.example.type.StatusType;

public class ObjectForm {
    private TestArrayForm testArray;
    private TestForm test;
    private StatusType statusType;
    private TestWrapperForm testWrapper;


    public TestArrayForm getTestArray() {
        return testArray;
    }

    public void setTestArray(TestArrayForm testArray) {
        this.testArray = testArray;
    }

    public TestForm getTest() {
        return test;
    }

    public void setTest(TestForm test) {
        this.test = test;
    }

    public TestWrapperForm getTestWrapper() {
        return testWrapper;
    }

    public void setTestWrapper(TestWrapperForm testWrapper) {
        this.testWrapper = testWrapper;
    }


    @Override
    public String toString() {
        return "ObjectModel{" +
                "testArray=" + testArray +
                ", test=" + test +
                ", testWrapper=" + testWrapper +
                '}';
    }
}
