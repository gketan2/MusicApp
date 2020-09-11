# Music App
A simple music streaming Android App.</br>
Streaming from the freeware server(yet to selected).

##### Using Following for Tech Stack:
>[Hilt (Dagger2)](https://dagger.dev/hilt/)</br>
[Retrofit 2](https://square.github.io/retrofit/)

##### Potential Music Streaming APIs:
>[Jamendo](https://developer.jamendo.com/v3.0)</br>
[Free Music Archive](https://freemusicarchive.org/)</br>

```diff
- Currently NO sophisticated Music Streaming API is present. Hence Stopping this for now.
```
##### Features Implemented
>Play Song Using Foreground Service.</br>
Handle Play/Pause from notification(By using Pending Intent).</br>
Save Currently Playing Song in SharedPreference so that next time user can replay that song from the last position.</br>
Play Song From List of Song(Currently fake since no good server found)</br>
Change Theme (Dark/ Light)</br>
UI using Neumorphism design provided by [fornewid](https://github.com/fornewid/neumorphism).</br>
