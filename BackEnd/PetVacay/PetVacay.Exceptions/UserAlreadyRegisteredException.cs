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
    public class UserAlreadyRegisteredException : Exception
    {
        public UserAlreadyRegisteredException()
        {
        }

        public UserAlreadyRegisteredException(string message) : base(message)
        {
        }

        public UserAlreadyRegisteredException(string message, Exception innerException) : base(message, innerException)
        {
        }

        protected UserAlreadyRegisteredException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
