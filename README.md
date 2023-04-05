# FairPlayClient
A ghost client that cheats against other cheaters

### This is WIP (work-in-progress) client
Features:
- Makes own calculations on client and notifies when a cheater is playing against you
- Mixin based
- Self-destruct
- Lunar Spoof (other players see you as Lunar Client player)

## Contributing

- Download LiquidBounce of desired version.
- Name it `liquidbounce.jar`
- Put it into the `libs` directory
- Open project in desired IDE (if you want to edit)
- Install Minecraft development environment (for IntelliJ)
  
  `gradlew clean setupDecompWorkspace getIntellijRuns`
- Build project
  
  `gradlew build`
  
   (you can also set target build directory by changing `copyDir` in `build.gradle.kts` and running `gradlew buildDev`)