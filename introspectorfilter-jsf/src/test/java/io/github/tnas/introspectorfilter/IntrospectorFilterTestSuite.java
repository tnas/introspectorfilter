package io.github.tnas.introspectorfilter;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({ IntrospectorFilterTest.class, CalibratedIntrospectorFilterTest.class })
public class IntrospectorFilterTestSuite {

}
