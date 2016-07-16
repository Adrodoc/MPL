install (
  /scoreboard objectives add POW dummy
  /scoreboard objectives setdisplay sidebar POW
)

uninstall (
  /scoreboard objectives remove POW
)

process testPow (
  /scoreboard players set base POW 5
  /scoreboard players set exponent POW 3
  /scoreboard players set result POW 1
  start pow
)

repeat process pow (
  /scoreboard players operation result POW *= base POW
  /scoreboard players remove exponent POW 1
  // This should test for 0 0 once this bug is fixed: https://bugs.mojang.com/browse/MC-97060
  /scoreboard players test exponent POW * 1
  conditional: stop
)
