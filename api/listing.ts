// MUST HAVE
const create = () => { };               // USER               POST   /api/listing
const get = ({page, pageSize}): Listing[] => { };  // USER               GET    /api/listing
const get = (): Listing => { };                  // USER               GET    /api/listing/:listing-id
const getBookmarked = (): Listing[] => { };        // USER (self)        GET    /api/listing/bookmarked
const update = () => { };               // USER (owner/admin) PATCH  /api/listing/:listing-id
const delete = () => { };               // USER (owner/admin) DELETE /api/listing/:listing-id
const reserve = () => { };              // USER (not author)  POST   /api/listing/:listing-id/reservation

// PERCHANCE?
const buy = () => { };                  // USER (not author)  POST /api/listing/:listing-id/buy


interface Listing {
  id: string; // uuid
  title: string;
  description: string;
  location: {
    lat: number;
    lon: number;
  };
  price: number;
  category: {
    name: string;
    id: string;
  };
  createdAt: Date;
  updatedAt: Date;
  author: {
    id: string;
    phone: string;
    name: string;
  },
  
  // Aggregate fields
  isBookmarked: boolean;
  bookmarks: number;
}












