/*
 * Copyright (C) 2011 Everit Kft. (http://www.everit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.everit.json.schema;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class CombinedSchemaTest {

  private static final List<Schema> SUBSCHEMAS = Arrays.asList(
      NumberSchema.builder().multipleOf(10).build(),
      NumberSchema.builder().multipleOf(3).build());

  @Test(expected = ValidationException.class)
  public void allCriterionFailure() {
    CombinedSchema.ALL_CRITERION.validate(10, 1);
  }

  @Test
  public void allCriterionSuccess() {
    CombinedSchema.ALL_CRITERION.validate(10, 10);
  }

  @Test(expected = ValidationException.class)
  public void anyCriterionFailure() {
    CombinedSchema.ANY_CRITERION.validate(10, 0);
  }

  @Test
  public void anyCriterionSuccess() {
    CombinedSchema.ANY_CRITERION.validate(10, 1);
  }

  @Test(expected = ValidationException.class)
  public void anyOfInvalid() {
    CombinedSchema.anyOf(Arrays.asList(
        StringSchema.builder().maxLength(2).build(),
        StringSchema.builder().minLength(4).build()))
        .build().validate("foo");
  }

  @Test
  public void factories() {
    CombinedSchema.allOf(Arrays.asList(BooleanSchema.INSTANCE));
    CombinedSchema.anyOf(Arrays.asList(BooleanSchema.INSTANCE));
    CombinedSchema.oneOf(Arrays.asList(BooleanSchema.INSTANCE));
  }

  @Test(expected = ValidationException.class)
  public void oneCriterionFailure() {
    CombinedSchema.ONE_CRITERION.validate(10, 2);
  }

  @Test
  public void oneCriterionSuccess() {
    CombinedSchema.ONE_CRITERION.validate(10, 1);
  }

  @Test
  public void validateAll() {
    TestSupport.failureOf(CombinedSchema.allOf(SUBSCHEMAS).build())
        .input(20)
        .expectedKeyword("allOf")
        .expect();
  }

  @Test
  public void validateAny() {
    TestSupport.failureOf(CombinedSchema.anyOf(SUBSCHEMAS).build())
        .input(5)
        .expectedKeyword("anyOf")
        .expect();
  }

  @Test
  public void validateOne() {
    TestSupport.failureOf(CombinedSchema.oneOf(SUBSCHEMAS).build())
        .input(30)
        .expectedKeyword("oneOf")
        .expect();
  }

  @Test
  public void reportCauses() {
    try {
      CombinedSchema.allOf(SUBSCHEMAS).build().validate(24);
      Assert.fail("did not throw exception");
    } catch (ValidationException e) {
      Assert.assertEquals(1, e.getCausingExceptions().size());
    }
  }

}
