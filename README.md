# Rebard

Rebard is a java application for playing back MIDI files in FFXIV using the Bard performance system. It's a fork of the already-excellent [TBbard](https://github.com/isalin/TBbard) with overhauled project structure, timing, and UI.


# Instructions

Notes can be extracted by dragging and dropping a MIDI file onto the application or by clicking "Open" and navigating to the intended file. You can then select the instrument to play back.

Alternatively, it's also possible to manually enter or edit the "sheets".

For the automated music playback feature to work:
1. In FFXIV, make sure the perform interface is open and that your character is ready to play.
2. Drag and drop the file to play, or navigate to the file by using the "Open" dialog (or enter your own notes!).
3. Click Play.
4. Tab back to FFXIV, and make sure it's the currently selected window.
5. Playback should start after the Start Delay.


## Full keyboard layout

If you want to use the full keyboard layout, make sure your keys are bound like this:
![Full keyboard layout](https://i.imgur.com/bGUNHwL.png)

It's highly recommended that you use the full keyboard layout to eliminate the delays required to reliably apply the modifier keys.

**Q: Why is the layout so weird?**

A: Because there are so many international keyboardlayouts, and this way they should all be supported (hopefully). 


## Syntax

* The application will understand of the following notes: C(-1), C, C(+1), C#(-1), C#, C#(+1), D(-1), D, D(+1), Eb(-1), Eb, Eb(+1), E(-1), E, E(+1), F(-1), F, F(+1), F#(-1), F#, F#(+1), G(-1), G, G(+1), G#(-1), G#, G#(+1), A(-1), A, A(+1), Bb(-1), Bb, Bb(+1), B(-1), B, B(+1).

* There is no need to write the parenthesis. "C-1" will work in place of "C(-1)".

* Line break is interpreted as a division between two notes. 

* Typos and malformed notes are simply ignored.

* The character "w" followed by numbers, is interpreted as a "wait" command. The numbers is the time to wait in *milliseconds*. For an example, "w3500" would mean waiting for 3500 milliseconds (3.5 seconds).

# Limitations

* Bards in FFXIV are limited to 3 octaves, whereas MIDI files are capable of playing back ~10 octaves. The application will attempt to convert this in a reasonable way. Unfortunately, some songs simply won't sound recognizable.

* If you're running the application with User Account Control active, you might have to run TBbard as administrator. To do this conveniently, use the .exe version, right-click it and select "Run as administrator".

* The drag and drop system doesn't work if the application is run as administrator.


# Compiling and Contributing

A big part of the reason for the fork was ditching JavaFX and migrating to a proper gradle project so that it's easy to modify and contribute. Just go into your favorite IDE, import the gradle project, and hack away! And if you've got improvements, I'd love to see them back here in the main branch!
