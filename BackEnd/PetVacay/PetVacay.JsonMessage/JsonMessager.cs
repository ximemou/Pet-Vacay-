using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.JsonMessage
{
    [ExcludeFromCodeCoverage]
    public class JsonMessager
    {
        public string Message { get; set; }

        public JsonMessager() { }

        public JsonMessager(string message)
        {
            Message = message;   
        }
    }
}
