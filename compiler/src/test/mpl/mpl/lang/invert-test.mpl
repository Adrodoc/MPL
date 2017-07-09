include "../../mpl/unit/mpl-unit.mpl"

#Test
impulse process test_invert_with_true {
  // given:
  /scoreboard players set MplTest MplTest 0

  // when:
  /say this is true
  invert: /scoreboard players set MplTest MplTest 1

  // then:
  /scoreboard players test MplTest MplTest 0 0
  conditional: notify continue_tests
  invert: start fail
}

#Test
impulse process test_invert_with_false {
  // given:
  /scoreboard players set MplTest MplTest 0

  // when:
  /this is false
  invert: /scoreboard players set MplTest MplTest 1

  // then:
  /scoreboard players test MplTest MplTest 1 1
  conditional: notify continue_tests
  invert: start fail
}
