using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.Exceptions
{
    [Serializable]
    [ExcludeFromCodeCoverage]
    public class PetAlreadyAddedException : Exception
    {
        public PetAlreadyAddedException()
        {
        }

        public PetAlreadyAddedException(string message) : base(message)
        {
        }

        public PetAlreadyAddedException(string message, Exception innerException) : base(message, innerException)
        {
        }

        protected PetAlreadyAddedException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
