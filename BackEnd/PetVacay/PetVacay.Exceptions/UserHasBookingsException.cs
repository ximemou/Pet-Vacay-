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
    public class UserHasBookingsException : Exception
    {
        public UserHasBookingsException()
        {
        }

        public UserHasBookingsException(string message) : base(message)
        {
        }

        public UserHasBookingsException(string message, Exception innerException) : base(message, innerException)
        {
        }

        protected UserHasBookingsException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
