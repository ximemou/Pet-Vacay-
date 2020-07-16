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
    public class WrongDataTypeException : Exception
    {
        public WrongDataTypeException()
        {
        }

        public WrongDataTypeException(string message) : base(message)
        {
        }

        public WrongDataTypeException(string message, Exception innerException) : base(message, innerException)
        {
        }

        protected WrongDataTypeException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
