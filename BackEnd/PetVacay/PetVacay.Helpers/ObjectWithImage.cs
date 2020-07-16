using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.Helpers
{
    [ExcludeFromCodeCoverage]
    public class ObjectWithImage
    {
        public object MyEntity { get; set; }
        public byte[] ProfileImage { get; set; }

        public ObjectWithImage(object entity, byte[] byteImage)
        {
            MyEntity = entity;
            ProfileImage = byteImage;
        }
    }
}
