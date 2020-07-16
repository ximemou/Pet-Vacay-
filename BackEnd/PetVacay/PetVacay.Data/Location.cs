using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.Data
{
    [ExcludeFromCodeCoverage]
    public class Location
    {
        public string Latitude { get; set; }
        public string Longitude { get; set; }

        public Location() { }

        public Location(string latitude, string longitude)
        {
            Latitude = latitude;
            Longitude = longitude;
        }

    }
}
