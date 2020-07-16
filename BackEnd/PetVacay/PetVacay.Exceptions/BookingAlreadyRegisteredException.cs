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
    public class BookingAlreadyRegisteredException : Exception
    {
        public BookingAlreadyRegisteredException()
        {
        }

        public BookingAlreadyRegisteredException(string message) : base(message)
        {
        }

        public BookingAlreadyRegisteredException(string message, Exception innerException) : base(message, innerException)
        {
        }

        protected BookingAlreadyRegisteredException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
