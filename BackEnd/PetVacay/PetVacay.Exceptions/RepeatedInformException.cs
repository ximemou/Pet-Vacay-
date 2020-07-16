﻿using System;
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
    public class RepeatedInformException : Exception
    {
        public RepeatedInformException()
        {
        }

        public RepeatedInformException(string message) : base(message)
        {
        }

        public RepeatedInformException(string message, Exception innerException) : base(message, innerException)
        {
        }

        protected RepeatedInformException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
