install {
  /scoreboard objectives add sneak stat.sneakTime
}

uninstall {
  /scoreboard objectives remove sneak
}

impulse process start_sneaking_game {
  /say starting new game
  /say sneak to win
  /scoreboard players reset @a sneak
  test_for_sneak()
  /say you won
}

repeat process test_for_sneak {
  /testfor @p[score_sneak_min=0]
  conditional: notify test_for_sneak
  conditional: stop
}
