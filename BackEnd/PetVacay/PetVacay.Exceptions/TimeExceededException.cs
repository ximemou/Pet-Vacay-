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
    public class TimeExceededException : Exception
    {
        public TimeExceededException()
        {
        }

        public TimeExceededException(string message) : base(message)
        {
        }

        public TimeExceededException(string message, Exception innerException) : base(message, innerException)
        {
        }

        protected TimeExceededException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
