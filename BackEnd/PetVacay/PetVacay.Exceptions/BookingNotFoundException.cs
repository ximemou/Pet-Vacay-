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
    public class BookingNotFoundException : Exception
    {
        public BookingNotFoundException()
        {
        }

        public BookingNotFoundException(string message) : base(message)
        {
        }

        public BookingNotFoundException(string message, Exception innerException) : base(message, innerException)
        {
        }

        protected BookingNotFoundException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
