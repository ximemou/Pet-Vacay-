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
    public class DisponibilitiesNotMatchException : Exception
    {
        public DisponibilitiesNotMatchException()
        {
        }

        public DisponibilitiesNotMatchException(string message) : base(message)
        {
        }

        public DisponibilitiesNotMatchException(string message, Exception innerException) : base(message, innerException)
        {
        }

        protected DisponibilitiesNotMatchException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
