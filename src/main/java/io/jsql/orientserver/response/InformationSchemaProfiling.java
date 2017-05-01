package io.jsql.orientserver.response;

import io.jsql.config.Fields;
import io.jsql.mysql.PacketUtil;
import io.jsql.mysql.mysql.EOFPacket;
import io.jsql.mysql.mysql.FieldPacket;
import io.jsql.mysql.mysql.ResultSetHeaderPacket;
import io.jsql.orientserver.OConnection;


public class InformationSchemaProfiling {

    private static final int FIELD_COUNT = 3;
    private static final ResultSetHeaderPacket header = PacketUtil.getHeader(FIELD_COUNT);
    private static final FieldPacket[] fields = new FieldPacket[FIELD_COUNT];
    private static final EOFPacket eof = new EOFPacket();


    /**
     * response method.
     *
     * @param c
     */
    public static void response(OConnection c) {


        int i = 0;
        byte packetId = 0;
        header.packetId = ++packetId;
        fields[i] = PacketUtil.getField("State", Fields.FIELD_TYPE_VAR_STRING);
        fields[i].packetId = ++packetId;
        fields[i + 1] = PacketUtil.getField("Duration", Fields.FIELD_TYPE_DECIMAL);
        fields[i + 1].packetId = ++packetId;

        fields[i + 2] = PacketUtil.getField("Percentage", Fields.FIELD_TYPE_DECIMAL);
        fields[i + 2].packetId = ++packetId;
        eof.packetId = ++packetId;
//        ByteBuffer buffer = c.allocate();

        // write header
//        buffer = header.write(buffer, c, true);
//
//        // write fields
//        for (FieldPacket field : fields) {
//            buffer = field.write(buffer, c, true);
//        }
//
//        // write eof
//        buffer = eof.write(buffer, c, true);
//
//        // write rows
//        packetId = eof.packetId;
//
//
//        // write last eof
//        EOFPacket lastEof = new EOFPacket();
//        lastEof.packetId = ++packetId;
//        buffer = lastEof.write(buffer, c, true);
//
//        // post write
//        c.write(buffer);


    }


}
