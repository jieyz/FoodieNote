//
// Created by jieyz on 2016/1/5.
//
#include "mediascanner.h"
//#include "iconv/iconv.h"
#include <stdio.h>
#include <stdlib.h>
#include "oscl_config/oscl_types.h"
#include "oscl_config/oscl_utf8conv.h"
#include "oscl_config/oscl_stdstring.h"
#include "oscl_config/oscl_mem.h"
#include "oscl_config/oscl_string_containers.h"
#include "oscl_config/oscl_string_utf8.h"
#include "oscl_config/oscl_file_io.h"
#include "pvmi/pvmf_return_codes.h"
#include "pvmi/pv_id3_parcom_constants.h"
#include "pvmi/pv_id3_parcom.h"
#include "pvmi/imp3ff.h"
#include <android/log.h>
#define LOG_TAG "System.out"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

#define MAX_BUFF_SIZE   1024
#define MAX_STR_LEN    1000
namespace android {
    MediaScanner::MediaScanner() {
    }

    MediaScanner::~MediaScanner() {
    }

    static PVMFStatus parseMP3(const char *filename) {
        PVID3ParCom pvId3Param;
        PVFile fileHandle;
        Oscl_FileServer iFs;
        uint32 duration;
        LOGD("c---> filename 好的 = %s",filename);
        if (iFs.Connect() != 0) {
            //LOGE("iFs.Connect failed\n");
            return PVMFFailure;
        }

        oscl_wchar output[MAX_BUFF_SIZE];
        oscl_UTF8ToUnicode((const char *) filename, oscl_strlen((const char *) filename),
                           (oscl_wchar *) output, MAX_BUFF_SIZE);
        if (0 !=
            fileHandle.Open((oscl_wchar *) output, Oscl_File::MODE_READ | Oscl_File::MODE_BINARY,
                            iFs)) {
            //LOGE("Could not open the input file for reading(Test: parse id3).\n");
            return PVMFFailure;
        }

        fileHandle.Seek(0, Oscl_File::SEEKSET);
        pvId3Param.ParseID3Tag(&fileHandle);
        fileHandle.Close();
        iFs.Close();

        //Get the frames information from ID3 library
        PvmiKvpSharedPtrVector framevector;
        pvId3Param.GetID3Frames(framevector);

        uint32 num_frames = framevector.size();

        for (uint32 i = 0; i < num_frames; i++) {
            const char *key = framevector[i]->key;
            bool isUtf8 = false;
            bool isIso88591 = false;

            // type should follow first semicolon
            const char *type = strchr(key, ';');
            if (type == NULL) continue;
            type++;

            char tracknumkeybuf[100];
            if (strncmp(key, "track-info/track-number;", 24) == 0) {
                // Java expects the track number key to be called "tracknumber", so
                // construct a temporary one here.
                snprintf(tracknumkeybuf, sizeof(tracknumkeybuf), "tracknumber;%s", type);
                key = tracknumkeybuf;
            }

            const char *value = framevector[i]->value.pChar_value;

            // KVP_VALTYPE_UTF8_CHAR check must be first, since KVP_VALTYPE_ISO88591_CHAR
            // is a substring of KVP_VALTYPE_UTF8_CHAR.
            // Similarly, KVP_VALTYPE_UTF16BE_WCHAR must be checked before KVP_VALTYPE_UTF16_WCHAR
            if (oscl_strncmp(type, KVP_VALTYPE_UTF8_CHAR, KVP_VALTYPE_UTF8_CHAR_LEN) == 0) {
                isUtf8 = true;
            } else if (
                    oscl_strncmp(type, KVP_VALTYPE_ISO88591_CHAR, KVP_VALTYPE_ISO88591_CHAR_LEN) ==
                    0) {
                isIso88591 = true;
            }

            if (isUtf8) {
                // validate to make sure it is legal utf8
                uint32 valid_chars;
                if (oscl_str_is_valid_utf8((const uint8 *) value, valid_chars)) {
                    // utf8 can be passed through directly
                    LOGD("c---> key(utf8) = %s  ; value = %s",key,value);
                    //if (!client.handleStringTag(key, value)) goto failure;
                } else {
                    // treat as ISO-8859-1 if UTF-8 fails
                    isIso88591 = true;
                }
            }

            // treat it as iso-8859-1 and our native encoding detection will try to
            // figure out what it is
            if (isIso88591) {
                // convert ISO-8859-1 to utf8, worse case is 2x inflation
                const unsigned char *src = (const unsigned char *) value;
                char *temp = (char *) alloca(strlen(value) * 2 + 1);
                if (temp) {
                    char *dest = temp;
                    unsigned int uch;
                    while ((uch = *src++) != 0) {
                        if (uch & 0x80) {
                            *dest++ = (uch >> 6) | 0xc0;
                            *dest++ = (uch & 0x3f) | 0x80;
                        } else *dest++ = uch;
                    }
                    *dest = 0;
                    LOGD("c---> key(iso) = %s  ; value = %s",key,temp);
                    //if (!client.addStringTag(key, temp)) goto failure;
                }
            }

            // not UTF-8 or ISO-8859-1, try wide char formats
            if (!isUtf8 && !isIso88591 &&
                (oscl_strncmp(type, KVP_VALTYPE_UTF16BE_WCHAR, KVP_VALTYPE_UTF16BE_WCHAR_LEN) ==
                 0 ||
                 oscl_strncmp(type, KVP_VALTYPE_UTF16_WCHAR, KVP_VALTYPE_UTF16_WCHAR_LEN) == 0)) {
                // convert wchar to utf8
                // the id3parcom library has already taken care of byteswapping
                const oscl_wchar *src = framevector[i]->value.pWChar_value;
                int srcLen = oscl_strlen(src);
                // worse case is 3 bytes per character, plus zero termination
                int destLen = srcLen * 3 + 1;
                char *dest = (char *) alloca(destLen);

                if (oscl_UnicodeToUTF8(src, oscl_strlen(src), dest, destLen) > 0) {
                    LOGD("c---> key(!utf8 && !iso) = %s  ; value = %s",key,dest);
                    //if (!client.addStringTag(key, dest)) goto failure;
                }
            } else if (oscl_strncmp(type, KVP_VALTYPE_UINT32, KVP_VALTYPE_UINT32_LEN) == 0) {
                char temp[20];
                snprintf(temp, sizeof(temp), "%d", (int) framevector[i]->value.uint32_value);
                LOGD("c---> key() = %s  ; value = %s",key,temp);
                //if (!client.addStringTag(key, temp)) goto failure;
            } else {
                //LOGE("unknown tag type %s for key %s\n", type, key);
            }
        }

        // extract non-ID3 properties below
        {
            OSCL_wHeapString<OsclMemAllocator> mp3filename(output);
            MP3ErrorType err;
            IMpeg3File mp3File(mp3filename, err);
            if (err != MP3_SUCCESS) {
                //LOGE("IMpeg3File constructor returned %d for %s\n", err, filename);
                return err;
            }
            err = mp3File.ParseMp3File();
            if (err != MP3_SUCCESS) {
                //LOGE("IMpeg3File::ParseMp3File returned %d for %s\n", err, filename);
                return err;
            }

            char buffer[20];
            duration = mp3File.GetDuration();
            sprintf(buffer, "%d", duration);
            LOGD("c---> duration = %s","duration");
            //if (!client.addStringTag("duration", buffer)) goto failure;
        }

        return PVMFSuccess;

        failure:
        return PVMFFailure;
    }

    void MediaScanner::processFile(
            const char *path){
        parseMP3(path);
    }
}
