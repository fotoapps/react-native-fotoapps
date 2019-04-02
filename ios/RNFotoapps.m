
#import "RNFotoapps.h"

@implementation RNFotoapps

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}
RCT_EXPORT_MODULE()

RCT_REMAP_METHOD(recognize,
                 path:(NSString*)path
                 recognizeWithResolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject)
{
    resolve(@"A");
}

@end

