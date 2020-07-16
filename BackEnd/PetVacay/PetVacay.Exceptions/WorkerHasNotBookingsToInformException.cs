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
    public class WorkerHasNotBookingsToInformException : Exception
    {
        public WorkerHasNotBookingsToInformException()
        {
        }

        public WorkerHasNotBookingsToInformException(string message) : base(message)
        {
        }

        public WorkerHasNotBookingsToInformException(string message, Exception innerException) : base(message, innerException)
        {
        }

        protected WorkerHasNotBookingsToInformException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
