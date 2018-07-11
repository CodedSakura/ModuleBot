# ModuleBot
A bot to (eventually) use modules

# Features
## Modularity
Admins can enable and disable modules to limit the bot's usability
## Command 'API'
You can write new modules for this bot with ease, using the Command Class, see actual commands for usage
### `CommandHost`
Extend `CommandHost` to get access to most Guild events  
Implement `CH` if that is not needed
### `Command`
You are expected to always extend `Command` since it handles Message sending, etc.
# Running
## Database
Bot is based on a MySQL database, and while it CAN be changed, probably isn't that easy
### Samples
I have included 3 home-generated samples for testing purposes, feel free to remove them  
All samples are stored in `samples`, the can be almost any audio format  
Please make sure that no 2 samples share the same filename (with the extension removed), it would make the samples inaccessible
## Launch
The bot takes in **5** parameters: token, DataBase name, DB user, DB password, DB IP  
Obviously these can be hardcoded inside the bot, in `modulebot.main.Main`
# Future of the bot
I am planning on having a `modules` directory, where modules can be but at runtime, compiled as an simple `.jar`  
### Planned modules
- [ ] Independent sample module
- [ ] Google Search module
### Other ideas
I'd love to implement an alias feature, but it probably is too late now anyway...