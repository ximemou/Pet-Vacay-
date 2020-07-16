using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.Helpers
{
    [ExcludeFromCodeCoverage]
    public class ImageToReturn
    {
        public byte[] Image { get; set; }

        public ImageToReturn(byte [] img)
        {
            Image = img;
        }
    }
}
