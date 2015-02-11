#import <UIKit/UIKit.h>
#import "bistri/conference.h"

@class ViewController;
@interface AppDelegate : UIResponder<   ConferenceDelegate,
                                            PeerStreamDelegate,
                                            DataStreamDelegate,
                                            UIApplicationDelegate>

@property(strong, nonatomic) UIWindow* window;
@property(strong, nonatomic) ViewController* viewController;

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions;

- (void)close;
- (void)join: (NSString*) room;

@end
