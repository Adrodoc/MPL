include "../../mpl/unit/mpl-unit.mpl"
import "../../mpl/test/test-utils.mpl"

#Test
impulse process test_intercept {
  // given:
  /scoreboard players set MplTest MplTest 0

  // when:
  start setting_MplTest_to_5_and_calling_add_one_to_MplTest
  intercept add_one_to_MplTest

  // then:
  /scoreboard players test MplTest MplTest 5 5
  conditional: notify continue_tests
  invert: start fail
}

#Test
impulse process test_conditional_intercept_with_true {
  // given:
  /scoreboard players set MplTest MplTest 0

  // when:
  start setting_MplTest_to_5_and_calling_add_one_to_MplTest
  /say this is true
  conditional: intercept add_one_to_MplTest

  // then:
  /scoreboard players test MplTest MplTest 5 5
  conditional: notify continue_tests
  invert: start fail
}

#Test
impulse process test_conditional_intercept_with_false {
  // given:
  /scoreboard players set MplTest MplTest 0

  // when:
  start setting_MplTest_to_5_and_calling_add_one_to_MplTest
  /this is false
  conditional: intercept add_one_to_MplTest

  // then:
  /scoreboard players test MplTest MplTest 0 0
  conditional: notify continue_tests
  invert: start fail
}

#Test
impulse process test_invert_intercept_with_true {
  // given:
  /scoreboard players set MplTest MplTest 0

  // when:
  start setting_MplTest_to_5_and_calling_add_one_to_MplTest
  /say this is true
  invert: intercept add_one_to_MplTest

  // then:
  /scoreboard players test MplTest MplTest 0 0
  conditional: notify continue_tests
  invert: start fail
}

#Test
impulse process test_invert_intercept_with_false {
  // given:
  /scoreboard players set MplTest MplTest 0

  // when:
  start setting_MplTest_to_5_and_calling_add_one_to_MplTest
  /this is false
  invert: intercept add_one_to_MplTest

  // then:
  /scoreboard players test MplTest MplTest 5 5
  conditional: notify continue_tests
  invert: start fail
}
