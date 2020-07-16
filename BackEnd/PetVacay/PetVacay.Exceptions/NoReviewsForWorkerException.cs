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
    public class NoReviewsForWorkerException : Exception
    {
        public NoReviewsForWorkerException()
        {
        }

        public NoReviewsForWorkerException(string message) : base(message)
        {
        }

        public NoReviewsForWorkerException(string message, Exception innerException) : base(message, innerException)
        {
        }

        protected NoReviewsForWorkerException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
