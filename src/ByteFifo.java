
import java.io.IOException;

public class ByteFifo {

    byte[] data;
    int head, tail;
    int size;

    /**
     * Creates a new <code>ByteFifo</code> with <code>size</code> entries.
     *
     * @param size The size of the queue (size = 2^x - 1)
     */
    public ByteFifo(int size) {
        data = new byte[size + 1];
        this.size = size;
    }

    /**
     * Inserts one <code>byte</code> into the queue.
     * @param data <code>Byte</code> which will be inserted into the queue
     */
    public void enqueue(byte data) {
        if (availToWrite() > 0) {
            this.data[tail] = data;
            tail = (tail + 1) % this.data.length;
        }
    }

    /**
     * Removes one <code>byte</code> from the queue.
     * @return The removed byte.
     * @throws IOException
     *            if reading from an empty queue.
     */
    public byte dequeue() throws IOException {
        if (head != tail) {
            byte c= data[head];
            head = (head + 1) % data.length;
            return c;
        } else throw new IOException("IOException");
    }


    /**
     * Clears the queue.
     */
    public void clear() {
        head = tail;
    }

    /**
     * Reads the available entries in the queue.
     *
     * @return The available <code>bytes</code> to read.
     */
    public int availToRead() {
        int len = tail - head;
        if(len < 0) return data.length + len;
        return len;
    }

    /**
     * Reads the available space left in the queue.
     *
     * @return The available queue space.
     */
    public int availToWrite() {
        int len = tail - head;
        if(len < 0) len = data.length + len;
        return size - len;
    }

    /**
     * Reads the maximum number of entries in the queue.
     *
     * @return The size of the queue.
     */
    public int getSize() {
        return size;
    }
}
