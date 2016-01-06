#ifndef PV_MEDIA_SCANNER_H_

#define PV_MEDIA_SCANNER_H_

namespace android {

struct MediaScanner
{
public:
    MediaScanner();
    virtual ~MediaScanner();

    virtual void processFile(
            const char *path);

private:
    MediaScanner(const MediaScanner &);
    MediaScanner &operator=(const MediaScanner &);
};

}  // namespace android

#endif  // PV_MEDIA_SCANNER_H_
