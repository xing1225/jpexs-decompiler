package com.jpexs.decompiler.flash.iggy.conversion;

import com.jpexs.decompiler.flash.SWF;
import com.jpexs.decompiler.flash.iggy.IggyChar;
import com.jpexs.decompiler.flash.iggy.IggyCharNode;
import com.jpexs.decompiler.flash.types.FILLSTYLEARRAY;
import com.jpexs.decompiler.flash.types.LINESTYLE;
import com.jpexs.decompiler.flash.types.LINESTYLEARRAY;
import com.jpexs.decompiler.flash.types.RGB;
import com.jpexs.decompiler.flash.types.SHAPE;
import com.jpexs.decompiler.flash.types.shaperecords.CurvedEdgeRecord;
import com.jpexs.decompiler.flash.types.shaperecords.EndShapeRecord;
import com.jpexs.decompiler.flash.types.shaperecords.SHAPERECORD;
import com.jpexs.decompiler.flash.types.shaperecords.StraightEdgeRecord;
import com.jpexs.decompiler.flash.types.shaperecords.StyleChangeRecord;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JPEXS
 */
public class IggyCharToShapeConvertor {

    private static int convertDistanceX(double val) {
        return (int) (20.0 * val * 1024.0);
    }

    private static int convertDistanceY(double val) {
        return (int) (20.0 * val * 1024.0);
    }

    public static SHAPE convertCharToShape(IggyChar igchar) {
        SHAPE shape = new SHAPE();
        List<SHAPERECORD> retList = new ArrayList<>();
        List<IggyCharNode> ignodes = igchar.getNodes();

        int prevX = 0;
        int prevY = 0;

        for (IggyCharNode ign : ignodes) {
            if (ign.getNodeType() == IggyCharNode.NODE_TYPE_MOVE) {
                StyleChangeRecord scr = new StyleChangeRecord();
                scr.stateMoveTo = true;
                prevX = scr.moveDeltaX = convertDistanceX(ign.getX1());
                prevY = scr.moveDeltaY = convertDistanceY(ign.getY1());
                scr.fillStyles = new FILLSTYLEARRAY();
                scr.lineStyles = new LINESTYLEARRAY();
                scr.calculateBits();
                retList.add(scr);
            } else {

                int curX1 = convertDistanceX(ign.getX1());
                int curY1 = convertDistanceY(ign.getY1());

                int curX2 = convertDistanceX(ign.getX2());
                int curY2 = convertDistanceY(ign.getY2());

                if (ign.getNodeType() == IggyCharNode.NODE_TYPE_LINE_TO) {
                    StraightEdgeRecord ser = new StraightEdgeRecord();
                    ser.deltaX = curX1 - prevX;
                    ser.deltaY = curY1 - prevY;
                    ser.generalLineFlag = true;
                    ser.simplify();
                    ser.calculateBits();
                    prevX = curX1;
                    prevY = curY1;
                    retList.add(ser);
                } else if (ign.getNodeType() == IggyCharNode.NODE_TYPE_CURVE_POINT) {
                    CurvedEdgeRecord cer = new CurvedEdgeRecord();
                    cer.controlDeltaX = curX2 - prevX;
                    cer.controlDeltaY = curY2 - prevY;
                    cer.anchorDeltaX = curX1 - curX2;
                    cer.anchorDeltaY = curY1 - curY2;
                    prevX = curX1;
                    prevY = curY1;
                    cer.calculateBits();
                    retList.add(cer);
                }
            }
        }

        StyleChangeRecord init;
        if (!retList.isEmpty() && retList.get(0) instanceof StyleChangeRecord) {
            init = (StyleChangeRecord) retList.get(0);
        } else {
            init = new StyleChangeRecord();
            retList.add(0, init);
        }

        retList.add(new EndShapeRecord());
        init.stateFillStyle1 = true;
        init.fillStyle1 = 1;
        shape.shapeRecords = retList;
        shape.numFillBits = 1;
        shape.numLineBits = 0;

        return shape;
    }
}
