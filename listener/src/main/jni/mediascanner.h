#ifndef PV_MEDIA_SCANNER_H_

#define PV_MEDIA_SCANNER_H_

#include <stdint.h>

namespace android {
    class MediaScannerClient;
    struct MediaScanner {
    public:
        MediaScanner();

        virtual ~MediaScanner();

        virtual void processFile(
                const char *path,MediaScannerClient& client);

    private:
        MediaScanner(const MediaScanner &);

        MediaScanner &operator=(const MediaScanner &);
    };

    class MediaScannerClient {
    public:
        MediaScannerClient();
        virtual ~MediaScannerClient();

        virtual bool handleStringTag(const char *name, const char *value) = 0;

    protected:
        uint32_t mLocaleEncoding;
    };
}  // namespace android

#endif  // PV_MEDIA_SCANNER_H_
