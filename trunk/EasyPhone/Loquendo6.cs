using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;

namespace Loquendo
{
    public enum AudioDestination
    {
        AudioBoard = 0,
        AudioFile
    }
    public enum InputType
    {
        Buffer = 1,
        File = 2,
        Default = 0
    }

    public enum TextCoding
    {
        Ansi = 1,
        Iso = 2,
        Unicode = 3,
        Utf8 = 4,
        AutoDetect = 10,
        Default = 0
    }

    public enum ReadingMode
    {
        Multiline = 1,
        Paragraph = 2,
        SSML = 3,
        XML = 4,
        AutoDetect = 10,
        Default = 0
    }

    public enum ProcessingMode
    {
        NonBlocking = 1,
        Blocking = 2,
        Slice = 3,
        Default = 0
    }

    public class Loquendo6
    {
        static IntPtr session = IntPtr.Zero;
        static IntPtr instance = IntPtr.Zero;
        static IntPtr voice = IntPtr.Zero;

        public static string[] AudioDestName = new string[] {
			"LoqAudioBoard",
			"LoqAudioFile"
		};

        const int tts_OK = 0;
        const byte ttsTRUE = 1;
        const byte ttsFALSE = 0;

        static string CODING = "loq210";
        static uint SAMPLE_RATE = 16000;

        const string dllName = "LoqTTS6.dll";


        [DllImport(dllName)]
        static extern uint ttsNewSession(ref IntPtr hSession, string IniFile);

        public static void NewSession(string IniFile)
        {
            uint result = Loquendo6.ttsNewSession(ref session, IniFile);

            if (result != tts_OK)
            {
                throw new System.ApplicationException("LoquendoTTS: Could not start a new session.");
            }
        }



        [DllImport(dllName)]
        static extern uint ttsQuery(IntPtr Handle, byte[] SelectedData, byte[] DataConditions, [Out] byte[] QueryResult, uint QueryResultLen, byte SearchInSystem);

        public static int Query(string SelectedData, string DataConditions, bool SearchInSystem, ref string Result)
        {
            byte[] ba = new byte[1024];

            uint result = Loquendo6.ttsQuery(session,
                UnicodeToByteArrayString(SelectedData),
                UnicodeToByteArrayString(DataConditions),
                ba,
                1024,
                (SearchInSystem ? ttsTRUE : ttsFALSE));

            Result = ByteArrayStringToUnicode(ba);

            return Convert.ToInt32(result);
        }



        [DllImport(dllName)]
        static extern uint ttsNewInstance(ref IntPtr hInstance, IntPtr hSession, byte[] IniFile);

        public static void NewInstance(string IniFile)
        {
            uint result = Loquendo6.ttsNewInstance(ref instance, session, UnicodeToByteArrayString(IniFile));
            if (result != Loquendo6.tts_OK)
                throw new ApplicationException("LoquendoTTS: Could not create a new instance - " + GetError(IntPtr.Zero));
        }



        [DllImport(dllName)]
        static extern IntPtr ttsGetError(IntPtr handle);

        public static string GetError(IntPtr handle)
        {
            IntPtr result = ttsGetError(handle);
            return PtrToStringAnsi(result);
        }



        [DllImport(dllName)]
        static extern uint ttsDeleteSession(IntPtr hSession);

        public static void DeleteSession()
        {
            // check for an open session
            if (session == IntPtr.Zero)
                return;

            uint result = Loquendo6.ttsDeleteSession(session);
            if (result != tts_OK)
                throw new ApplicationException("LoquendoTTS: Could not delete current session.");
        }


        /*[DllImport(dllName)]
        static extern uint ttsSetInstanceParam(IntPtr hInstance, string paramName, string paramValue);

        public static void SetInstanceParam(string paramName, string paramValue)
        {
            uint result = TTS.ttsSetInstanceParam(instance, paramName, paramValue);
        }*/


        [DllImport(dllName)]
        static extern uint ttsNewVoice(ref IntPtr voice, IntPtr hInstance, byte[] Speaker, uint SampleRate, byte[] coding);

        public static void NewDefaultVoice(string speaker)
        {
            NewVoice(speaker, SAMPLE_RATE, CODING);
        }

        public static void NewVoice(string Speaker, uint SampleRate, string Coding)
        {
            uint result = ttsNewVoice(ref voice, instance, UnicodeToByteArrayString(Speaker), SampleRate, UnicodeToByteArrayString(Coding));

            if (result != tts_OK)
            {
                StringBuilder msg = new StringBuilder();
                msg.AppendFormat(null, "LoquendoTTS: Could not create a new voice (speaker={0}, sample_rate={1}, coding={2}) - {3}", Speaker, SampleRate.ToString(), Coding, GetError(IntPtr.Zero));
                throw new ApplicationException(msg.ToString());
            }

        }



        [DllImport(dllName)]
        static extern uint ttsSetAudio(IntPtr hInstance, byte[] AudioDestName, byte[] AudioDeviceName,
            byte[] coding, IntPtr pUser);

        public static void SetAudio(AudioDestination Destination, string DeviceName)
        {
            // Note: in WinCE.NET the only supported audio output coding is linear ("l")
            // (see Loquendo TTS for Windows CE.NET documentation)

            uint result = ttsSetAudio(instance,
                UnicodeToByteArrayString(AudioDestName[(int)Destination]),
                UnicodeToByteArrayString(DeviceName),
                UnicodeToByteArrayString("l"),
                IntPtr.Zero);

            if (result != tts_OK)
            {
                throw new ApplicationException("LoquendoTTS: error while setting audio properties - " + GetError(instance));
            }
        }



        [DllImport(dllName)]
        static extern uint ttsRead(IntPtr hInstance, byte[] Input, InputType input, TextCoding coding,
            ReadingMode readMode, ProcessingMode procMode);

        public static void Read(string input, InputType type, TextCoding coding,
            ReadingMode readMode, ProcessingMode procMode)
        {
            byte[] inputBytes;

            if (coding == TextCoding.Unicode)
                inputBytes = new UnicodeEncoding().GetBytes(input + '\0');
            else
                inputBytes = UnicodeToByteArrayString(input);

            uint result = ttsRead(instance,
                 inputBytes,
                 type,
                 coding,
                 readMode,
                 procMode);

            if (result != tts_OK)
                throw new ApplicationException("LoquendoTTS: could not execute ttsRead - " + GetError(instance));
        }

        public static void Read(string input)
        {
            //Read(input, InputType.Buffer, TextCoding.Unicode, ReadingMode.SSML, ProcessingMode.NonBlocking);
            Read(input, InputType.Buffer, TextCoding.Unicode, ReadingMode.AutoDetect, ProcessingMode.NonBlocking);
        }

        public static void Spell(string input)
        {
            string s = "";

            for (int i = 0; i < input.Length; i++)
            {
                s += input[i] + " ";
            }
            Read(s);
        }

        public static void SpellSync(string input)
        {
            string s = "";

            for (int i = 0; i < input.Length; i++)
            {
                s += input[i] + " ";
            }
            Read(s, InputType.Buffer, TextCoding.Unicode, ReadingMode.AutoDetect,
                    ProcessingMode.Blocking);
        }

        [DllImport(dllName)]
        static extern uint ttsStop(IntPtr hInstance);

        public static void Stop()
        {
            ttsStop(instance);
        }

        #region Utility functions

        static byte[] UnicodeToByteArrayString(string str)
        {
            if (str == null)
                return null;

            byte[] result = new byte[str.Length + 1];

            for (int i = 0; i < str.Length; i++)
            {
                byte[] charBytes = BitConverter.GetBytes(str[i]);
                result[i] = charBytes[0];
            }

            // insert a terminating '\0' character in the string
            result[str.Length] = 0;

            return result;
        }

        static string ByteArrayStringToUnicode(byte[] bytes)
        {
            if (bytes == null)
                return null;

            StringBuilder sb = new StringBuilder(bytes.Length - 1);
            foreach (byte b in bytes)
            {
                if (b == 0)
                    break;
                sb.Append((char)b);
            }
            return sb.ToString();

        }

        static string PtrToStringAnsi(IntPtr pointer)
        {
            StringBuilder result = new StringBuilder();
            int index = 0;
            do
            {
                byte currByte = Marshal.ReadByte(pointer, index++);

                if (currByte == 0)
                    break;

                result.Append((char)currByte);

            } while (true);

            return result.ToString();
        }

        #endregion
    }
}
