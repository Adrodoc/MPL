include "../../mpl/unit/mpl-unit.mpl"

#Test
impulse process test_while_repeat_with_false {
  // given:
  /scoreboard players set MplTest MplTest 1

  // when:
  while: /this is false
  repeat {
    /scoreboard players add MplTest MplTest 1
  }

  // then:
  /scoreboard players test MplTest MplTest 1 1
  conditional: notify continue_tests
  invert: start fail
}

#Test
impulse process test_while_repeat_with_5_iterations {
  // given:
  /scoreboard players set MplTest MplTest 1

  // when:
  while: /scoreboard players test MplTest MplTest 1 5
  repeat {
    /scoreboard players add MplTest MplTest 1
  }

  // then:
  /scoreboard players test MplTest MplTest 6 6
  conditional: notify continue_tests
  invert: start fail
}

#Test
impulse process test_repeat_while_with_false {
  // given:
  /scoreboard players set MplTest MplTest 1

  // when:
  repeat {
    /scoreboard players add MplTest MplTest 1
  } do while: /this is false

  // then:
  /scoreboard players test MplTest MplTest 2 2
  conditional: notify continue_tests
  invert: start fail
}

#Test
impulse process test_repeat_while_with_5_iterations {
  // given:
  /scoreboard players set MplTest MplTest 1

  // when:
  repeat {
    /scoreboard players add MplTest MplTest 1
  } do while: /scoreboard players test MplTest MplTest 1 5

  // then:
  /scoreboard players test MplTest MplTest 6 6
  conditional: notify continue_tests
  invert: start fail
}

#Test
impulse process test_repeat_with_unconditional_break {
  // given:
  /scoreboard players set MplTest MplTest 1

  // when:
  repeat {
    /scoreboard players add MplTest MplTest 1
    break
  }

  // then:
  /scoreboard players test MplTest MplTest 2 2
  conditional: notify continue_tests
  invert: start fail
}

#Test
impulse process test_repeat_with_conditional_break_after_5_iterations {
  // given:
  /scoreboard players set MplTest MplTest 1

  // when:
  repeat {
    /scoreboard players add MplTest MplTest 1
    /scoreboard players test MplTest MplTest 6 *
    conditional: break
  }

  // then:
  /scoreboard players test MplTest MplTest 6 6
  conditional: notify continue_tests
  invert: start fail
}

#Test
impulse process test_repeat_with_invert_break_after_5_iterations {
  // given:
  /scoreboard players set MplTest MplTest 1

  // when:
  repeat {
    /scoreboard players add MplTest MplTest 1
    /scoreboard players test MplTest MplTest 1 5
    invert: break
  }

  // then:
  /scoreboard players test MplTest MplTest 6 6
  conditional: notify continue_tests
  invert: start fail
}

#Test
impulse process test_nested_break_with_label {
  // given:
  /scoreboard players set MplTest MplTest 1

  // when:
  outer: repeat {
    repeat {
      break outer
    }
    /scoreboard players add MplTest MplTest 1
  }

  // then:
  /scoreboard players test MplTest MplTest 1 1
  conditional: notify continue_tests
  invert: start fail
}

#Test
impulse process test_conditional_continue_checks_the_condition {
  // given:
  /scoreboard players set MplTest MplTest 1

  // when:
  repeat {
    /scoreboard players add MplTest MplTest 1
    /scoreboard players test MplTest MplTest 1 5
    conditional: continue
  } do while: /this is false

  // then:
  /scoreboard players test MplTest MplTest 2 2
  conditional: notify continue_tests
  invert: start fail
}

#Test
impulse process test_invert_continue_checks_the_condition {
  // given:
  /scoreboard players set MplTest MplTest 1

  // when:
  repeat {
    /scoreboard players add MplTest MplTest 1
    /scoreboard players test MplTest MplTest 6 *
    invert: continue
  } do while: /this is false

  // then:
  /scoreboard players test MplTest MplTest 2 2
  conditional: notify continue_tests
  invert: start fail
}

#Test
impulse process test_repeat_with_conditional_continue_for_5_iterations {
  // given:
  /scoreboard players set MplTest MplTest 1

  // when:
  repeat {
    /scoreboard players add MplTest MplTest 1
    /scoreboard players test MplTest MplTest 1 5
    conditional: continue
    break
  }

  // then:
  /scoreboard players test MplTest MplTest 6 6
  conditional: notify continue_tests
  invert: start fail
}

#Test
impulse process test_repeat_with_invert_continue_for_5_iterations {
  // given:
  /scoreboard players set MplTest MplTest 1

  // when:
  repeat {
    /scoreboard players add MplTest MplTest 1
    /scoreboard players test MplTest MplTest 6 *
    invert: continue
    break
  }

  // then:
  /scoreboard players test MplTest MplTest 6 6
  conditional: notify continue_tests
  invert: start fail
}

#Test
impulse process test_nested_continue_with_label {
  // given:
  /scoreboard players set MplTest MplTest 1

  // when:
  outer: while: /scoreboard players test MplTest MplTest * 1
  repeat {
    /scoreboard players add MplTest MplTest 1
    repeat {
      continue outer
    }
    /scoreboard players add MplTest MplTest 10
  }

  // then:
  /scoreboard players test MplTest MplTest 2 2
  conditional: notify continue_tests
  invert: start fail
}
