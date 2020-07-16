using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.Helpers
{
    [ExcludeFromCodeCoverage]
    public class ImageSaver
    {
        private static ImageSaver saver;

        private ImageSaver() { }

        public static ImageSaver GetIntance()
        {
            if(saver == null)
            {
                saver = new ImageSaver();
            }
            return saver;
        }

        public string getUrlOfImage(sbyte[] profileImage, string eId, string namePrefix)
        {
            byte[] convertedProfileImage = (byte[])profileImage.Cast<byte>();
            string path = "C:\\profileImages\\" + namePrefix + eId + ".jpg";

            FileStream stream = new FileStream(path, FileMode.Create, FileAccess.Write);
            stream.Write(convertedProfileImage, 0, profileImage.Length);
            stream.Close();

            return path;
        }
        public byte[] getImage(string path)
        {
            if(path == null)
            {
                return null;
            }
            FileStream stream = File.OpenRead(path);
            byte[] profileImage = new byte[stream.Length];

            stream.Read(profileImage, 0, (int)stream.Length);
            stream.Close();

            return profileImage;
        }
    }
}
