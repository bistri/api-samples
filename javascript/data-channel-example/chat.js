var room;
var users = {};
var dataChannels = {};

// when Bistri API client is ready, function
// "onBistriConferenceReady" is invoked
onBistriConferenceReady = function () {

    // test if the browser is WebRTC compatible
    if ( !bc.isCompatible() ) {
        // if the browser is not compatible, display an alert
        alert( "your browser is not WebRTC compatible !" );
        // then stop the script execution
        return;
    }

    /* Set events handler */

    // when local user is connected to the server
    bc.signaling.bind( "onConnected", function () {
        // show pane with id "pane_1"
        showPanel( "pane_1" );
    } );

    // when an error occured on the server side
    bc.signaling.bind( "onError", function () {
        // display an alert message
        alert( error.text + " (" + error.code + ")" );
    } );

    // when the user has joined a room
    bc.signaling.bind( "onJoinedRoom", function ( data ) {
        // set the current room name
        room = data.room;
        // show pane with id "pane_2"
        showPanel( "pane_2" );
        // then, for every single members already present in the room ...
        for ( var i=0, max=data.members.length; i<max; i++ ) {
            // set a couple id/nickname to "users" object
            users[ data.members[ i ].id ] = data.members[ i ].name;
            // ... open a data channel
            bc.openDataChannel( data.members[ i ].id, "chat", data.room );
        }
    } );

    // when an error occurred while trying to join a room
    bc.signaling.bind( "onJoinRoomError", function ( error ) {
        // display an alert message
        alert( error.text + " (" + error.code + ")" );
    } );

    // when the local user has quitted the room
    bc.signaling.bind( "onQuittedRoom", function( room ) {
        // show pane with id "pane_1"
        showPanel( "pane_1" );
        // stop the local stream
        bc.stopStream();
    } );

    // when a remote user has joined a room in which the local user is in
    bc.signaling.bind( "onPeerJoinedRoom", function ( peer ) {
        // set a couple id/nickname to "users" object
        users[ peer.pid ] = peer.name;
    } );

    // when a remote user has quitted a room in which the local user is in
    bc.signaling.bind( "onPeerQuittedRoom", function ( peer ) {
        // delete couple id/nickname in "users" object
        delete users[ peer.pid ];
    } );

    // when the local user has created a data channel, invoke "onDataChannel" callback
    bc.channels.bind( "onDataChannelCreated", onDataChannel );

    // when the remote user has created a data channel, invoke "onDataChannel" callback
    bc.channels.bind( "onDataChannelRequested", onDataChannel );

    // bind function "setNickname" to button "Set Nickname"
    q( "#nick" ).addEventListener( "click", setNickname );

    // bind function "joinChatRoom" to button "Join Chat Room"
    q( "#join" ).addEventListener( "click", joinChatRoom );

    // bind function "quitChatRoom" to button "Quit Chat Room"
    q( "#quit" ).addEventListener( "click", quitChatRoom );

    // bind function "sendChatMessage" to button "Send Message"
    q( "#send" ).addEventListener( "click", sendChatMessage );

}

// when "onDataChannelCreated" or "onDataChannelRequested" are triggered
function onDataChannel( dataChannel, remoteUserId ){

    // when the data channel is open
    dataChannel.onOpen = function(){
        // set a couple id/datachannel to "dataChannels" object
        dataChannels[ remoteUserId ] = this;
        // check chat partner presence
        isThereSomeone();
    };

    // when the data channel is closed
    dataChannel.onClose = function(){
        // delete couple id/datachannel from "dataChannels" object
        delete dataChannels[ remoteUserId ];
        // check chat partner presence
        isThereSomeone();
    };

    // when a message is received from the data channel
    dataChannel.onMessage = function( event ){
        // display the received message
        displayMessage( users[ remoteUserId ], event.data );
    };

}

// when button "Set Nickname" has been clicked
function setNickname(){
    // get nickname field content
    var nickname = q( "#nick_field" ).value;
    // if a nickname has been set ...
    if( nickname ){
        // initialize API client with application keys and nickname
        // if you don't have your own, you can get them at:
        // https://api.developers.bistri.com/login
        bc.init( {
            appId: "38077edb",
            appKey: "4f304359baa6d0fd1f9106aaeb116f33",
            userName: nickname,
            debug: true
        } );
        // open a new session on the server
        bc.connect();
    }
    else{
        // otherwise, display an alert
        alert( "you must enter a nickname !" );
    }
}

// when button "Join Chat Room" has been clicked
function joinChatRoom(){
    // get chat room field content
    var roomToJoin = q( "#room_field" ).value;
    // if a chat room name has been set ...
    if( roomToJoin ){
        // ... join the room
        bc.joinRoom( roomToJoin );
    }
    else{
        // otherwise, display an alert
        alert( "you must enter a room name !" );
    }
}

// when button "Quit Chat Room" has been clicked
function quitChatRoom(){
    // for each data channel present in "dataChannels" object ...
    for( var id in dataChannels ){
        // ... close the data channel
        dataChannels[ id ].close();
    }
    // and quit chat room
    bc.quitRoom( room );
}

// when button "Send Message" has been clicked
function sendChatMessage(){
    // get message field content
    var message = q( "#message_field" ).value;
    // if a chat room name has been set ...
    if( message ){
        // for each data channel present in "dataChannels" object ...
        for( var id in dataChannels ){
             // ... send a message
            dataChannels[ id ].send( message );
        }
        // display the sent message
        displayMessage( "me", message );
        // reset message field content
        q( "#message_field" ).value = "";
    }
}

// when a message must be dislpayed
function displayMessage( user, message ){
    // create a message node and insert it in div#messages_container node
    var container = q( "#messages_container" );
    var textNode = document.createTextNode( user + " > " + message );
    var node = document.createElement( "div" );
    node.className = "message";
    node.appendChild( textNode );
    container.appendChild( node );
    // scroll to bottom to always display the last message
    container.scrollTop = container.scrollHeight;
}

// when checking for chat partner presence
function isThereSomeone(){
    // if "dataChannels" object contains one or more data channel objects ...
    if( Object.keys( dataChannels ).length ){
        // ... enabled "Send Message" button
        q( "#send" ).removeAttribute( "disabled" );
    }
    else{
        // otherwise disable "Send Message" button
        q( "#send" ).setAttribute( "disabled", "disabled" );
    }
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