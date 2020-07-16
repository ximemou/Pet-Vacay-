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
    public class InsufficientDataException : Exception
    {
        public InsufficientDataException()
        {
        }

        public InsufficientDataException(string message) : base(message)
        {
        }

        public InsufficientDataException(string message, Exception innerException) : base(message, innerException)
        {
        }

        protected InsufficientDataException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
