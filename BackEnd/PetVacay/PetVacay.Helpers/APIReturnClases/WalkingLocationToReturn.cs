using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.Helpers.APIReturnClases
{
    [ExcludeFromCodeCoverage]
    public class WalkingLocationToReturn
    {
        public string InitialLatitude { get; set; }
        public string InitialLongitude { get; set; }
        public string CurrentLatitude { get; set; }
        public string CurrentLongitude { get; set; }

        public WalkingLocationToReturn() { }
        public WalkingLocationToReturn(string iLat, string iLong, string fLat, string fLong)
        {
            InitialLatitude = iLat;
            InitialLongitude = iLong;
            CurrentLatitude = fLat;
            CurrentLongitude = fLong;
        }
    }
}
