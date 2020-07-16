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
    public class RepeatedReviewException : Exception
    {
        public RepeatedReviewException()
        {
        }

        public RepeatedReviewException(string message) : base(message)
        {
        }

        public RepeatedReviewException(string message, Exception innerException) : base(message, innerException)
        {
        }

        protected RepeatedReviewException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
