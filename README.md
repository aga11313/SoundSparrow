SoundSparrow
============

SoundSparrow is a hackathon project by [Agnieszka Wasikowska](https://github.com/aga11313), [Mayank Gupta](https://github.com/officialgupta) and [Cameron MacLeod](https://github.com/notexactlyawe). It was undertaken at HackCambridge 2017. It allows the user to have a unique song that changes based on their mood and to share this song with others, whilst hearing other songs at the same time.

Reasoning
---------

The project was decided upon as we wanted to gain more experience with Android, and we are interested in both automatic music generation and Bluetooth. We decided we wanted to do a project that we could get excited over as opposed to a project that would be 'useful' as an app. This enabled us to get more excited about the building of it.

Function
--------

The project consists of an app and an Azure blob storage backend. Whilst we didn't manage to finish all of it, the basic flow was imagined as follows. The app would welcome the user, take a picture of them and upload it to our Azure backend. The URL of this image would then be sent to the Microsoft Cognitive Services API which would return us the emotions of the face within the photo. We would then use this emotion with a UUID of the phone to generate music that would be played to the user. Whilst the music was being played, the app would be both scanning for BLE advertisements and advertising itself. Whenever an advertisement from another Sparrow (the app) was discovered, the emotion of that user combined with their UUID would be lifted from the advertisement and used to generate the other user's music that would be played alongside the current user's music. This would enable a unique 'symphony' for each set of users combined with the set of their emotions.
