#import "ViewController.h"
#import "AppDelegate.h"

@interface ViewController ()

@property(nonatomic, assign) UIInterfaceOrientation statusBarOrientation;

@end

@implementation ViewController{
    NSMutableDictionary* mediaStreams;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.statusBarOrientation = [UIApplication sharedApplication].statusBarOrientation;
    
    self.roomInput.delegate = self;
    [self.roomInput becomeFirstResponder];
    
    self.view.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"wood.png"]];
    
    mediaStreams = [[NSMutableDictionary alloc] init];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    [self startRendering];
}

- (void)viewWillDisappear:(BOOL)animated {
    [self stopRendering];
    [super viewDidAppear:animated];
}

- (void)startRendering {
    [self resizeVideoViews];
}

- (void)stopRendering {
    NSArray* keys = [mediaStreams allKeys];
    for (int i=0; i<keys.count; i++) {
        NSString* id = keys[i];
        MediaStream* ms = [ mediaStreams valueForKey:id ];
        
        [ms releaseVideoView];
    }
}

- (void)addMediaStream: (MediaStream*)mediaStream {
    NSLog(@"addMediaStream pid:%@", [ [mediaStream getPeerStream] getId ] );
    
    [mediaStreams setObject:mediaStream forKey:[ [mediaStream getPeerStream] getId ] ];
    mediaStream.delegate = self;
    
    [self resizeVideoViews];

    [self.view addSubview:[ [ mediaStream getVideoView ] getView] ];
}

- (void)removePeerStream: (PeerStream*)peerStream {

    NSString* id = [peerStream getId];
    
    [ [ [ [mediaStreams valueForKey:id] getVideoView ] getView] removeFromSuperview ];

    [mediaStreams removeObjectForKey:id];
    
    [self resizeVideoViews];
}

- (void)sizeVideoView:(VideoView*) vv ratio:(float)ratio top:(int)top left:(int)left width:(int)maxWidth height:(int)maxHeight{
    
    int height = maxHeight;
    int width = height * ratio;
    if ( width > maxWidth) {
        width = maxWidth;
        height = maxWidth / ratio;
    }
    
    int centeredTop = top + (maxHeight-height) /2;
    int centeredLeft = left + (maxWidth-width) /2;
    
    CGRect frame = CGRectMake(0, 0, width, height);

    frame.origin = CGPointMake( centeredLeft, centeredTop );

    [vv getView].frame = frame;
}

- (void)resizeVideoViews {

    CGRect screenRect = [[UIScreen mainScreen] bounds];
    CGFloat screenWidth = screenRect.size.width;
    CGFloat screenHeight = screenRect.size.height;
    NSArray* keys = [mediaStreams allKeys];
    int count = keys.count;
    
    if ( !count ) return;
    
    int maxHeight = screenHeight / ( count/2 + count%2 );
    int maxWidth = screenWidth / ( (count>1) ? 2 : 1 );
    
    for (int i=0; i<count; i++) {
        NSString* id = keys[i];
        MediaStream* ms = [ mediaStreams valueForKey:id ];
        
        [self sizeVideoView:[ms getVideoView] ratio:[ms getVideoRatio] top:(i/2)*maxHeight left:(i%2)*maxWidth width:maxWidth height:maxHeight ];
    }
}

- (void)onVideoRatioChange:(float)ratio media:(MediaStream *)mediaStream {
    [self resizeVideoViews];
}

- (void)resetUI {
    [self.roomInput resignFirstResponder];
    self.roomInput.text = nil;
    self.roomInput.hidden = NO;
    self.blackView.hidden = YES;
}

#pragma mark - UITextFieldDelegate

- (void)textFieldDidEndEditing:(UITextField*)textField {
    NSString* room = textField.text;
    if ([room length] == 0) {
        return;
    }
    textField.hidden = YES;
    
    AppDelegate *appDelegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    [appDelegate join:room];
}

- (BOOL)textFieldShouldReturn:(UITextField*)textField {
    [textField resignFirstResponder];
    return YES;
}

-(NSUInteger)supportedInterfaceOrientations {
 return UIInterfaceOrientationMaskAll;
}

- (void)viewDidLayoutSubviews {
    if (self.statusBarOrientation != [UIApplication sharedApplication].statusBarOrientation) {
        self.statusBarOrientation = [UIApplication sharedApplication].statusBarOrientation;
        [[NSNotificationCenter defaultCenter]
         postNotificationName:@"StatusBarOrientationDidChange"
         object:nil ];

        [ self resizeVideoViews ];
    }
}



@end
