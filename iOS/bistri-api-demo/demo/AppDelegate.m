#import <AVFoundation/AVFoundation.h>

#import "AppDelegate.h"

#import "ViewController.h"

@interface AppDelegate ()

@property(nonatomic, strong) Conference* conference;

@end

@implementation AppDelegate;

#pragma mark - UIApplicationDelegate methods

- (BOOL)application:(UIApplication*)application
didFinishLaunchingWithOptions:(NSDictionary*)launchOptions {
    
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    self.viewController =
    [[ViewController alloc] initWithNibName:@"ViewController"
                                         bundle:nil];
    self.window.rootViewController = self.viewController;
    
    [self.window makeKeyAndVisible];
    
    self.conference = [ Conference getInstance ];
    [ self.conference setDelegate:self];
    [ self.conference setInfoWithAppID: @"38077edb" APIKey: @"4f304359baa6d0fd1f9106aaeb116f33" userName: @"iosDemo" ];
    [ self.conference connect ];
    [ self.conference setLoudspeaker:true ];
    
    return YES;
}

- (void)applicationWillResignActive:(UIApplication*)application {
    //  Application lost focus, connection broken
}

- (void)applicationDidEnterBackground:(UIApplication*)application {
    [self.viewController stopRendering];
}

- (void)applicationWillEnterForeground:(UIApplication*)application {
}

- (void)applicationDidBecomeActive:(UIApplication*)application {
    [self.viewController startRendering];
}

- (void)applicationWillTerminate:(UIApplication*)application {
    [self close];
}

#pragma mark - internal methods

- (void)disconnect {
    [self.conference disconnect];
}


#pragma mark - ConferenceDelegate methods

// Implement ConferenceDelegate
- (void)onConnectionEvent:(Connection)status {
    NSString* statusString = @"Unknown";
    switch ( status ) {
        case DISCONNECTED:
            statusString = @"DISCONNECTED";
            break;
        case CONNECTING:
            statusString = @"CONNECTING";
            break;
        case CONNECTING_SENDREQUEST:
            statusString = @"CONNECTING_SENDREQUEST";
            break;
        case CONNECTED:
            statusString = @"CONNECTED";
            break;
    }
    
    NSLog(@"onConnectionEvent status:%@", statusString);
}

// Implement ConferenceDelegate
- (void)onError:(Error)error {
    NSString* errorString = @"Unknown";
    switch ( error ) {
        case NO_ERROR:
            errorString = @"NO_ERROR";
            break;
        case CONNECTION_ERROR:
            errorString = @"CONNECTION_ERROR";
            break;
    }
    
    NSLog( @"onError error:%@", errorString );
}

// Implement ConferenceDelegate
- (void)onRoomJoined:(NSString *)room_name {
    NSLog( @"onRoomJoined room name:%@", room_name );
}

// Implement ConferenceDelegate
-(void)onRoomMembers:(NSString *)room members:(NSArray *)members{
    NSLog( @"onRoomMembers room name:%@ members count:%d", room, members.count );
}

// Implement ConferenceDelegate
- (void)onRoomQuitted:(NSString*)roomName {
    NSLog( @"onRoomQuitted %@", roomName );
}

// Implement ConferenceDelegate
- (void)onNewPeer:(PeerStream *)peerStream {
    NSLog( @"onNewPeer peer id:%@", [ peerStream getId ] );
    [peerStream setDelegate: self];
}

// Implement ConferenceDelegate
- (void)onRemovedPeer:(PeerStream *)peerStream {
    NSLog( @"onRemovedPeer peer id:%@", [ peerStream getId ] );
    
    [self.viewController removePeerStream:peerStream];
}

// Implement ConferenceDelegate
-(void)onIncomingRequest:(NSString *)peerId name:(NSString *)peerName room:(NSString *)room callEvent:(NSString *)event {
    NSLog( @"onIncomingRequest peer id:%@ name:%@ room:%@ event:%@", peerId, peerName, room, event );
}

// Implement ConferenceDelegate
-(void)onPresence:(NSString *)peerId presence:(Presence)presence{
    NSLog( @"onIncomingRequest peer id:%@ presence:%@", peerId, [Conference presenceToString:presence] );
}

// Implement PeerStreamDelegate
- (void)onMediaStream:(MediaStream *)mediaStream peerId:(NSString *)peerId {
    NSLog( @"onMediaStream peer id:%@", peerId );
    
    [self.viewController addMediaStream:mediaStream];
}
// Implement PeerStreamDelegate
- (void)onDataStream:(DataStream *)dataStream peerId:(NSString *)peerId {
    NSLog( @"onMediaStream peer id:%@", peerId );
    
    dataStream.delegate = self;
}

// Implement DataStreamDelegate
-(void)onOpen:(DataStream *)dataStream {
    NSLog( @"DataStreamDelegate onOpen" );
}

// Implement DataStreamDelegate
-(void)onError:(DataStream *)dataStream error:(NSString *)error {
    NSLog( @"DataStreamDelegate onError error:%@", error );
}

// Implement DataStreamDelegate
-(void)onClose:(DataStream *)dataStream {
    
}

// Implement DataStreamDelegate
-(void)onMessage:(DataStream *)dataStream message:(NSData *)message isBinary:(BOOL)binary {
    NSLog( @"DataStreamDelegate onMessage isBinary:%@", binary?@"YES" : @"NO" );
    NSString* text = [[NSString alloc] initWithData:message encoding:NSUTF8StringEncoding];
    if (binary) {
        NSLog( @"message:%@", text );
    }
    text = [NSString stringWithFormat:@"(ios echo) %@", text];
    [dataStream send:text];
}

#pragma mark - public methods

- (void)close {
    [self disconnect];
    [self.viewController resetUI];
}

- (void)join: (NSString*) room{
    [self.conference join:room];
}

@end
