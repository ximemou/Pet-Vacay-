using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.DTO
{
    [ExcludeFromCodeCoverage]
    public class LoginDTO
    {
        public string Email { get; set; }
        public string Password { get; set; }
        public LoginDTO() { }
    }
}
