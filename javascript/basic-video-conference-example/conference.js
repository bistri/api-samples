// when Bistri API client is ready, function
 // "onBistriConferenceReady" is invoked
 onBistriConferenceReady = function () {

     // initialize API client with application keys
     // if you don't have your own, you can get them at:
     // https://api.developers.bistri.com/login
     BistriConference.init( {
         appId: "38077edb",
         appKey: "4f304359baa6d0fd1f9106aaeb116f33"
     } );

     // test if the browser is WebRTC compatible
     if ( !BistriConference.isCompatible() ) {
         // if the browser is not compatible, display an alert
         alert( "your browser is not WebRTC compatible !" );
         // then stop the script execution
         return;
     }

     /* Set events handler */

     // when local user is connected to the server
     BistriConference.signaling.addHandler( "onConnected", function () {
         // ask the user to access to his webcam
         BistriConference.startStream( "webcamSD", function( localStream ){
              // insert the local webcam stream into the page body
             BistriConference.attachStream( localStream, document.body );
             // join a conference room called "conference_demo"
             BistriConference.joinRoom( "conference_demo" );
         });
     });

     // when the user has joined a room
     BistriConference.signaling.addHandler( "onJoinedRoom", function ( result ) {
         // set room members array in a var "roomMembers"
         var roomMembers = result.members;
          // then, for every single members already present in the room ...
         for ( var i=0, max=roomMembers.length; i<max; i++ ) {
             // ... request a call
             BistriConference.call( roomMembers[i].id, "conference_demo" );
         }
     });

     // when a new remote stream is received
     BistriConference.streams.addHandler( "onStreamAdded", function ( remoteStream ) {
         // insert the new remote stream into the page body
         BistriConference.attachStream( remoteStream, document.body );
     });

     // when a stream has been stopped
     BistriConference.streams.addHandler( "onStreamClosed", function ( stream ) {
         // remove the stream from the page
         BistriConference.detachStream( stream );
     } );

     // open a new session on the server
     BistriConference.connect();

 }