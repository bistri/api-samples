var room;

// when Bistri API client is ready, function
// "onBistriConferenceReady" is invoked
onBistriConferenceReady = function () {

    // test if the browser is WebRTC compatible
    if ( !BistriConference.isCompatible() ) {
        // if the browser is not compatible, display an alert
        alert( "your browser is not WebRTC compatible !" );
        // then stop the script execution
        return;
    }

    // initialize API client with application keys
    // if you don't have your own, you can get them at:
    // https://api.developers.bistri.com/login
    BistriConference.init( {
        appId: "38077edb",
        appKey: "4f304359baa6d0fd1f9106aaeb116f33"
    } );

    /* Set events handler */

    // when local user is connected to the server
    BistriConference.signaling.addHandler( "onConnected", function () {
        // show pane with id "pane_1"
        showPanel( "pane_1" );
    } );

    // when an error occured on the server side
    BistriConference.signaling.addHandler( "onError", function ( error ) {
        // display an alert message
        alert( error.text + " (" + error.code + ")" );
    } );

    // when the user has joined a room
    BistriConference.signaling.addHandler( "onJoinedRoom", function ( data ) {
        // set the current room name
        room = data.room;
        // ask the user to access to his webcam
        BistriConference.startStream( "webcamSD", function( localStream ){
            // when webcam access has been granted
            // show pane with id "pane_2"
            showPanel( "pane_2" );
            // insert the local webcam stream into div#video_container node
            BistriConference.attachStream( localStream, q( "#video_container" ) );
            // then, for every single members present in the room ...
            for ( var i=0, max=data.members.length; i<max; i++ ) {
                // ... request a call
                BistriConference.call( data.members[ i ].id, data.room );
            }
        } );
    } );

    // when an error occurred while trying to join a room
    BistriConference.signaling.addHandler( "onJoinRoomError", function ( error ) {
        // display an alert message
       alert( error.text + " (" + error.code + ")" );
    } );

    // when the local user has quitted the room
    BistriConference.signaling.addHandler( "onQuittedRoom", function( room ) {
        // show pane with id "pane_1"
        showPanel( "pane_1" );
        // stop the local stream
        BistriConference.stopStream();
    } );

    // when a new remote stream is received
    BistriConference.streams.addHandler( "onStreamAdded", function ( remoteStream ) {
        // insert the new remote stream into div#video_container node
        BistriConference.attachStream( remoteStream, q( "#video_container" ) );
    } );

    // when a local or a remote stream has been stopped
    BistriConference.streams.addHandler( "onStreamClosed", function ( stream ) {
        // remove the stream from the page
        BistriConference.detachStream( stream );
    } );

    // bind function "joinConference" to button "Join Conference Room"
    q( "#join" ).addEventListener( "click", joinConference );

    // bind function "quitConference" to button "Quit Conference Room"
    q( "#quit" ).addEventListener( "click", quitConference );

    // open a new session on the server
    BistriConference.connect();
}

// when button "Join Conference Room" has been clicked
function joinConference(){
    var roomToJoin = q( "#room_field" ).value;
    // if "Conference Name" field is not empty ...
    if( roomToJoin ){
        // ... join the room
        BistriConference.joinRoom( roomToJoin );
    }
    else{
        // otherwise, display an alert
        alert( "you must enter a room name !" )
    }
}

// when button "Quit Conference Room" has been clicked
function quitConference(){
    // quit the current conference room
    BistriConference.quitRoom( room );
}

function showPanel( id ){
    var panes = document.querySelectorAll( ".pane" );
    // for all nodes matching the query ".pane"
    for( var i=0, max=panes.length; i<max; i++ ){
        // hide all nodes except the one to show
        panes[ i ].style.display = panes[ i ].id == id ? "block" : "none";
    };
}

function q( query ){
    // return the DOM node matching the query
    return document.querySelector( query );
}