using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.Helpers
{
    [ExcludeFromCodeCoverage]
    public class LocationBundle
    {
        public string Latitude { get; set; }
        public string Longitude { get; set; }

        public LocationBundle() { }
        public LocationBundle(string latitude, string longitude)
        {
            Latitude = latitude;
            Longitude = longitude;
        }
    }
}
