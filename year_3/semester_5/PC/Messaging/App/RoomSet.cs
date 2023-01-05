using System.Collections.Concurrent;

namespace App
{
    /*
     * Manages a set of rooms, namely the creation and retrieval.
     * Thread-safe.
     */
    public class RoomSet
    {
        private readonly ConcurrentDictionary<string, Room> _rooms = new ConcurrentDictionary<string, Room>();
        
        public Room GetOrCreateRoom(string name)
        {
            return _rooms.GetOrAdd(name, new Room(name));
        }
    }
}