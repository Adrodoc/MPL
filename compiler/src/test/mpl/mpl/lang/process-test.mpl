include "../../mpl/unit/mpl-unit.mpl"
import "../../mpl/test/test-utils.mpl"

#Test
impulse process test_calling_a_remote_process_takes_two_ticks {
  // given:
  /scoreboard players set MplTest MplTest 0
  /scoreboard players set Tick MplTest 0
  start tick_counter

  // when:
  add_one_to_MplTest_impulse()

  // then:
  stop tick_counter
  /scoreboard players test MplTest MplTest 1 1
  conditional: /scoreboard players test Tick MplTest 2 2
  conditional: notify continue_tests
  invert: start fail
}

#Test
impulse process test_calling_an_inline_process_is_instantanious {
  // given:
  /scoreboard players set MplTest MplTest 0
  /scoreboard players set Tick MplTest 0
  start tick_counter

  // when:
  add_one_to_MplTest_inline()

  // then:
  stop tick_counter
  /scoreboard players test MplTest MplTest 1 1
  conditional: /scoreboard players test Tick MplTest 0 0
  conditional: notify continue_tests
  invert: start fail
}

#Test
impulse process test_conditional_call_an_inline_process_with_true {
  // given:
  /scoreboard players set MplTest MplTest 0

  // when:
  /say this is true
  conditional: add_one_to_MplTest_inline()

  // then:
  /scoreboard players test MplTest MplTest 1 1
  conditional: notify continue_tests
  invert: start fail
}

#Test
impulse process test_conditional_call_an_inline_process_with_false {
  // given:
  /scoreboard players set MplTest MplTest 0

  // when:
  /this is false
  conditional: add_one_to_MplTest_inline()

  // then:
  /scoreboard players test MplTest MplTest 0 0
  conditional: notify continue_tests
  invert: start fail
}

#Test
impulse process test_invert_call_an_inline_process_with_true {
  // given:
  /scoreboard players set MplTest MplTest 0

  // when:
  /say this is true
  invert: add_one_to_MplTest_inline()

  // then:
  /scoreboard players test MplTest MplTest 0 0
  conditional: notify continue_tests
  invert: start fail
}

#Test
impulse process test_invert_call_an_inline_process_with_false {
  // given:
  /scoreboard players set MplTest MplTest 0

  // when:
  /this is false
  invert: add_one_to_MplTest_inline()

  // then:
  /scoreboard players test MplTest MplTest 1 1
  conditional: notify continue_tests
  invert: start fail
}

#Test
impulse process test_then_call_an_inline_process_with_true {
  // given:
  /scoreboard players set MplTest MplTest 0

  // when:
  if: /say this is true
  then {
    add_one_to_MplTest_inline()
  }

  // then:
  /scoreboard players test MplTest MplTest 1 1
  conditional: notify continue_tests
  invert: start fail
}

#Test
impulse process test_then_call_an_inline_process_with_false {
  // given:
  /scoreboard players set MplTest MplTest 0

  // when:
  if: /this is false
  then {
    add_one_to_MplTest_inline()
  }

  // then:
  /scoreboard players test MplTest MplTest 0 0
  conditional: notify continue_tests
  invert: start fail
}

#Test
impulse process test_else_call_an_inline_process_with_true {
  // given:
  /scoreboard players set MplTest MplTest 0

  // when:
  if: /say this is true
  else {
    add_one_to_MplTest_inline()
  }

  // then:
  /scoreboard players test MplTest MplTest 0 0
  conditional: notify continue_tests
  invert: start fail
}

#Test
impulse process test_else_call_an_inline_process_with_false {
  // given:
  /scoreboard players set MplTest MplTest 0

  // when:
  if: /this is false
  else {
    add_one_to_MplTest_inline()
  }

  // then:
  /scoreboard players test MplTest MplTest 1 1
  conditional: notify continue_tests
  invert: start fail
}
