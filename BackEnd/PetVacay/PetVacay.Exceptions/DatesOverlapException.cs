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
    public class DatesOverlapException : Exception
    {
        public DatesOverlapException()
        {
        }

        public DatesOverlapException(string message) : base(message)
        {
        }

        public DatesOverlapException(string message, Exception innerException) : base(message, innerException)
        {
        }

        protected DatesOverlapException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
