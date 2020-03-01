<hr>
<h3 align="center">BlockProtectX</h3>
<p align="center">:earth_asia:</p>
<p align="center">Block Logging and Protecting blocks Plugin for Nukkit</p>
<hr>

This plugin enables your server to log all blocks activities (place, break, tap) in your server's levels and to protect blocks.  

## How to use
1. Run your server to create ./plugins/BlockProtectX/Config.yml  
2. Edit ./plugins/BlockProtectX/Config.yml  
```Config.yml
# Needed day counts for players to break blocks.
LoginDaysCount: 3

# Worlds list that players can break blocks despite their login days count.
ExceptLevels: ["world", "myWorld"]
```  
3. Rerun your server.  
  
## Commands
- /co  
***[Op only]*** Enable or Disable checking block logs mode.