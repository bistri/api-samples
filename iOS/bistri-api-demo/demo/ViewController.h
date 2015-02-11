#import <UIKit/UIKit.h>
#import "bistri/conference.h"


@interface ViewController : UIViewController<
    MediaStreamDelegate,
    UITextFieldDelegate>

@property(weak, nonatomic) IBOutlet UITextField* roomInput;
@property(weak, nonatomic) IBOutlet UIView* blackView;

- (void)resetUI;
- (void)addMediaStream: (MediaStream*)mediaStream;
- (void)removePeerStream: (PeerStream*)peerStream;
- (NSUInteger)supportedInterfaceOrientations;
- (void)startRendering;
- (void)stopRendering;
@end
