using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;

namespace PetVacay.Adapters
{
    public class GeocoderAdapter : IGeocoderAdapter
    {
        public int GetZipCodeFromAddress(string address)
        {
            var requestUri = string.Format("http://maps.googleapis.com/maps/api/geocode/xml?address={0}&sensor=false", Uri.EscapeDataString(address));

            var request = WebRequest.Create(requestUri);
            var response = request.GetResponse();
            var xdoc = XDocument.Load(response.GetResponseStream());

            var result = xdoc.Element("GeocodeResponse").Element("result");

            string zipCode = (string)result.Elements("address_component").Where(ac => ac.Elements("type")
            .Any(t => t.Value == "postal_code")).Elements("long_name").FirstOrDefault();

            try
            {
                return int.Parse(zipCode);
            }
            catch (FormatException)
            {
                return 0;
            }
        }
    }
}
